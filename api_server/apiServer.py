#!flask/bin/python
import json
from flask import Flask, jsonify, abort, make_response, request
from subprocess import *

def jarWrapper(*args):
    process = Popen(['java', '-jar']+list(args), stdout=PIPE, stderr=PIPE)
    ret = []
    while process.poll() is None:
        line = process.stdout.readline()
        if line != '' and line.endswith('\n'):
            ret.append(line[:-1])
    stdout, stderr = process.communicate()
    ret += stdout.split('\n')
    if stderr != '':
        ret += stderr.split('\n')
    ret.remove('')
    return ret

app = Flask(__name__)

settings = [
    {
        'id':1,
        'url': u'172.16.1.130',
        #'username': u'testname',
        #'password': u'testpwd', 
        'pan': u'0.0', 
        'tilt':u'0.0',
        'zoom':u'0.0'
    },
    {
        'id':2,
        'url': u'172.16.1.134',
        #'username': u'testname',
        #'password': u'testpwd', 
        'pan': u'0.0', 
        'tilt':u'0.0',
        'zoom':u'0.0'
    },
    {
        'id':3,
        'url': u'172.16.1.140',
        #'username': u'testname',
        #'password': u'testpwd', 
        'pan': u'0.0', 
        'tilt':u'0.0',
        'zoom':u'0.0'
    }
]

@app.route('/v1/camera/ptz/preset/create', methods=['POST'])
def create_preset():
   # if not request.json or not {'url', 'username', 'password', 'pan', 'tilt', 'zoom'} in request.json:
    if not request.json:
        abort(400)

    panNum = float(request.json.get('pan'))
    tiltNum = float(request.json.get('tilt'))
    zoomNum = float(request.json.get('zoom'))

    if not -1.0 <= panNum <= 1.0:
        abort(400)
    if not -1.0 <= tiltNum <= 1.0:
        abort(400)
    if not 0.0 <= zoomNum <= 2.0:
        abort(400)

    setting = {
        'id': settings[-1]['id'] + 1,
        'url': request.json.get('url'),
        #'username': request.json.get('username'),
        #'password': request.json.get('password'),
        'pan': request.json.get('pan'),
        'tilt': request.json.get('tilt'),
        'zoom': request.json.get('zoom'),
    }
    settings.append(setting)
    return jsonify({'status_code':'ok', 'status_message': setting}), 201

@app.route('/v1/camera/ptz', methods=['POST'])
def run_ptz_req():
    #if not request.json or not {'url', 'username', 'password', 'pan', 'tilt', 'zoom'} in request.json:
    if not request.json:
        abort(400)

    '''
    settings[0]['url'] = request.json.get('url', settings[0]['url'])
    settings[0]['username'] = request.json.get('username', settings[0]['username'])
    settings[0]['password'] = request.json.get('password', settings[0]['password'])
    settings[0]['pan'] = request.json.get('pan', settings[0]['pan'])
    settings[0]['tilt'] = request.json.get('tilt', settings[0]['tilt'])
    settings[0]['zoom'] = request.json.get('zoom', settings[0]['zoom'])
    '''
    
    panNum = float(request.json.get('pan'))
    tiltNum = float(request.json.get('tilt'))
    zoomNum = float(request.json.get('zoom'))

    if not -1.0 <= panNum <= 1.0:
        abort(400)
    if not -1.0 <= tiltNum <= 1.0:
        abort(400)
    if not 0.0 <= zoomNum <= 2.0:
        abort(400)

    #args = ['onvif.jar', settings[0]['url'], settings[0]['username'], settings[0]['password'], settings[0]['pan'], settings[0]['tilt'], settings[0]['zoom']] 
    args = ['onvif.jar', request.json.get('url'), request.json.get('username'), request.json.get('password'), request.json.get('pan'), request.json.get('tilt'), request.json.get('zoom')] 
    result = jarWrapper(*args)
    return jsonify({'status_code':'ok', 'status_message': result}), 200
    
@app.route('/v1/camera/ptz/preset/<int:setting_id>', methods=['POST'])
def post_setting(setting_id):
    setting = [setting for setting in settings if setting['id'] == setting_id]
    if len(setting) == 0:
        abort(404)
    args = ['onvif.jar', settings[setting_id-1]['url'], request.json.get('username'), request.json.get('password'), settings[setting_id-1]['pan'], settings[setting_id-1]['tilt'], settings[setting_id-1]['zoom']] 
    result = jarWrapper(*args)
    return jsonify({'status_code':'ok', 'status_message': result}), 200  

@app.route('/v1/camera/ptz/preset/<int:setting_id>', methods=['GET'])
def get_setting(setting_id):
    setting = [setting for setting in settings if setting['id'] == setting_id]
    if len(setting) == 0:
        abort(404)
    return jsonify({'settings': setting[0]}), 200

@app.route('/v1/camera/ptz/preset', methods=['GET'])
def get_settings():
    return jsonify({'settings': settings})

@app.errorhandler(404)
def not_found(error):
    return make_response(jsonify({'error': 'Not found'}), 404)

if __name__ == '__main__':
    app.run(debug=True, port=8888)
