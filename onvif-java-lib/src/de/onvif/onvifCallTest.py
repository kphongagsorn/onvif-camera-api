import os.path,subprocess
from subprocess import STDOUT,PIPE

def compile_java(java_file):
    subprocess.check_call(['javac', java_file])

def execute_java(java_file, stdin):
    java_class,ext = os.path.splitext(java_file)
    cmd = ['java', java_class]
    proc = subprocess.Popen(cmd, stdin=PIPE, stdout=PIPE, stderr=STDOUT)
    stdout,stderr = proc.communicate(stdin)
    #print ('This was "' + stdout + '"')
compile_java('Main.java')
execute_java('Main.java', '172.16.1.130', 'onvif', '33IoxkAwZ4R', '1.0', '1.0', '2.0', '1')
#execute_java('Main.class', '172.16.1.130', 'onvif', '33IoxkAwZ4R', '1.0', '1.0', '2.0', '1')
