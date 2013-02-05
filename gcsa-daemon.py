#!/usr/bin/python -u

import sys, time, os, commands, datetime, xml.dom.minidom as xml
from pymongo import MongoClient
from seth import Daemon

INTERVAL = 3
GCSA_ACCOUNTS = "/httpd/bioinfo/gcsa/accounts"

#Mongo configuration
mongoHost = "mem15"
mongoPort = 27017
mongoDatabase = "usertest"
mongoCollection = "users"

connection = MongoClient(mongoHost, mongoPort)
db = connection[mongoDatabase]
collection = db[mongoCollection]


def getXML():
    status, output = commands.getstatusoutput("qstat -xml")
    doc = xml.parseString(output)
    return doc.getElementsByTagName("job_list")

def getXMLattr(node, attribute):
    return node.attributes[attribute].value

def getTimeMillis():
    return datetime.datetime.now().strftime("%Y%m%d%H%M%S%f")[:-3]

def getLogTime():
    return datetime.datetime.now().strftime("%Y-%m-%d %a %H:%M:%S.%f")[:-3]

def getXMLtag(node, tag):
    return node.getElementsByTagName(tag)[0].childNodes[0].data

def getSGEJobInfo(JB_name):
    arr = JB_name.split('_',2)
    return (arr[0],arr[1])

def getSGEjobs():
    sgeJobs = {}
    for node in getXML():
        (toolName, jobId) = getSGEJobInfo(getXMLtag(node,"JB_name"))
        sgeJobs[jobId] = {
            "toolName":toolName,
            "state":getXMLattr(node,"state"),
            "s":getXMLtag(node,"state"),
        }
        #print (getXMLtag(node,"JB_job_number"))
        #print (getXMLtag(node,"JAT_prio"))
        #print (getXMLtag(node,"JB_name"))
        #print (getXMLtag(node,"JB_owner"))
        ##print (getXMLtag(node,"JAT_start_time"))
        ##print (getXMLtag(node,"queue_name"))
        #print (getXMLtag(node,"slots"))
    return sgeJobs

def getMongojobs():
    return collection.aggregate([{"$project":{"accountId":1,"jobs":1,"_id":0}},{"$unwind":"$jobs"},{"$match":{"jobs.visites":{"$lt":0}}}])["result"]

def task():
    sgeJobs = getSGEjobs()
    mongoResults = getMongojobs()
    for mongoResult in mongoResults:
        mongoJob = mongoResult["jobs"];
        mongoAccountId = mongoResult["accountId"]
        if mongoJob["id"] in sgeJobs:
            sgeJob = sgeJobs[mongoJob["id"]];
            sys.stdout.write(getLogTime()+"\t"+mongoJob["id"]+"\t"+sgeJob["s"]+"\t"+mongoAccountId+"\t")
            if sgeJob["s"] == "r" and mongoJob["status"]!="running":
                print(collection.update({"accountId":mongoAccountId,"jobs.id":mongoJob["id"]},{"$set":{"jobs.$.status":"running","jobs.$.visites":-1,"lastActivity":getTimeMillis()}}))
            elif sgeJob["s"] == "Eqw":
                print(collection.update({"accountId":mongoAccountId,"jobs.id":mongoJob["id"]},{"$set":{"jobs.$.status":"error","jobs.$.visites":0,"lastActivity":getTimeMillis()}}))
            #elif sgeJob["s"] == "qw":
                #print("qw")
            else:
                print("")
        else:
            sys.stdout.write(getLogTime()+"\t"+mongoJob["id"]+"\t"+mongoAccountId+"\t")
            #actualizar mongo, pq el job no esta en la sge, marcar como terminado, y ok o error  ----> esto no se sabe
            outdir = GCSA_ACCOUNTS+"/"+mongoAccountId+"/"+mongoJob["outdir"]
            status, output = commands.getstatusoutput("ls "+outdir+" | grep -v result.xml | grep -v sge_err.log | grep -v sge_out.log")
            sys.stdout.write(outdir+"\t")
            print(collection.update({"accountId":mongoAccountId,"jobs.id":mongoJob["id"]},{"$set":{"jobs.$.status":"finished","jobs.$.outputData":output.split("\n"),"jobs.$.visites":0,"lastActivity":getTimeMillis()}}))

def mongoDisconnect():
    connection.disconnect()


##########################################################
#################Daemon implementation####################
##########################################################
class MyDaemon(Daemon):

    def __init__(self, pid, log):
        Daemon.__init__(self, pid, stdout=log, stderr=log)

    def run(self):
        """Overrides Daemon().run() with actions you want to daemonize.
        MyDaemon.run() is then called within MyDaemon().start()"""
        print('Starting Deamon!')  # message issued on self.stdout
        while True:
            task()
            time.sleep(INTERVAL)
            #sys.stderr.write('error: unicode write test to stderr\n')
            #sys.stdout.write('write test to stdout\n')

    def shutdown(self):
        mongoDisconnect()
        """Overrides Daemon().shutdown() with some clean up"""
        print("Stopping Daemon!")  # message issued on self.stdout

if __name__ == '__main__':
    daemon = MyDaemon('/opt/gcsa-daemon/gcsa-daemon.pid','/opt/gcsa-daemon/gcsa-daemon.log')
    if len(sys.argv) == 2:
        if 'start' == sys.argv[1]:
            daemon.start()
        elif 'stop' == sys.argv[1]:
            daemon.stop()
        elif 'restart' == sys.argv[1]:
            daemon.restart()
        else:
            print('Unknown command')
            sys.exit(2)
            sys.exit(0)
    else:
        print("usage: {} start|stop|restart".format(sys.argv[0]))
        sys.exit(2)
