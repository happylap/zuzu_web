# encoding: utf-8

import logging
import tempfile
import os
import signal

logger = logging.getLogger("zuzu_single_process")

# Path to file storing PID of running script process.
# Place it outside of methods in the beggining, or make sure it visible for all methods, which use it.
pid_file = tempfile.gettempdir() + 'zuzu_notify_pid.txt'

# This method checks if file pid_file exists.
#  If it was found, depends of mode - exit, or kill process which PID is stored in file.
def scriptStarter(mode = 'force'):
    '''
        if mode  = force - kill runing script and run new one
        if mode != force - run script only if it's not already running
    '''
    if os.path.exists(pid_file):
        logger.info('old copy found')
        if mode == 'force':
            logger.info('running copy found, killing it')
            # reading PID from file and convert it to int
            pid = int((open(pid_file).read()))
            logger.info('pid have been read:'+ pid)
            # If you use pythton 2.7 or above just use os.kill to terminate PID process.
            # In case of python 2.6 run method killing selected PID
            kill(pid)
        else:
            logger.info('running copy found, leaving')
            # not force mode. Just don't run new copy. Leaving.
            raise SystemExit

 # If we are here, mode == force, old copy killed, writing PID for new script process
    open(pid_file, 'w').write(str(os.getpid()))

def kill(pid):
    os.kill(pid, signal.SIGTERM) #or signal.SIGKILL

# Delete pid-file befor script finish.
# Dont forget to run this method at the end of the script
# If you use PyQt call it in overloaded closeEvent()
def removePIDfile():
    logger.info('deleting pidfile')
    try:
        os.remove(pid_file)
    except OSError:
        pass
    return