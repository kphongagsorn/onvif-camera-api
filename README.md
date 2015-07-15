# onvif-camera-api
This is an API to control the Axis cameras.  The API server code passes paramaters to onvif.jar to select and control the camera.

#####Control options:
* Pan
* Tilt
* Zoom

##Example calls:
####View All Presets:
method: GET

url/v1/camera/ptz/preset

`curl -H "Content-Type: application/json" -X GET http://127.0.0.1:8888/v1/camera/ptz/preset`

####View a Selected Preset via ID: 
method: GET

url/v1/camera/ptz/preset/preset_id

`curl -H "Content-Type: application/json" -X GET http://127.0.0.1:8888/v1/camera/ptz/preset/1`

####Move Selected Camera via Preset ID:
method: POST

url/v1/camera/ptz/preset/preset_id

`curl -H "Content-Type: application/json" -X POST -d '{"username":"myUsername","password":"myPwd"}' http://127.0.0.1:8888/v1/camera/ptz/preset/4`

####Move Selected Camera:
method: POST

url/v1/camera/ptz/

`curl -H "Content-Type: application/json" -X POST -d '{"url":"172.16.1.140","username":"myUsername","password":"myPwd","pan":"-1.0","tilt":"-1.0","zoom":"2.0"}' http://127.0.0.1:8888/v1/camera/ptz`

####Create Preset:
method: POST

url/v1/camera/ptz/preset/create

`curl -H "Content-Type: application/json" -X POST -d '{"url":"172.16.1.140","pan":"-1.0","tilt":"1.0","zoom":"2.0"}' http://127.0.0.1:8888/v1/camera/ptz/preset/create`


##Example JSON Output:
####Presets:
```json
{
  "settings": [
    {
      "id": 1,
      "pan": "0.0",
      "tilt": "0.0",
      "url": "172.16.1.130",
      "zoom": "0.0"
    },
    {
      "id": 2,
      "pan": "0.0",
      "tilt": "0.0",
      "url": "172.16.1.134",
      "zoom": "0.0"
    },
    {
      "id": 3,
      "pan": "0.0",
      "tilt": "0.0",
      "url": "172.16.1.140",
      "zoom": "0.0"
    }
  ]
}
```
####Moving Camera:
```json
{
  "status_code": "ok",
  "status_message": [
    "x -1.0  y -1.0  zoom 2.0",
    "Move completed."
  ]
}
```
