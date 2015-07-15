package de.onvif;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.util.List;

import javax.xml.soap.SOAPException;

import org.onvif.ver10.schema.FloatRange;
import org.onvif.ver10.schema.H264Options;
import org.onvif.ver10.schema.ImagingSettings20;
import org.onvif.ver10.schema.IntRange;
import org.onvif.ver10.schema.JpegOptions;
import org.onvif.ver10.schema.Profile;
import org.onvif.ver10.schema.VideoEncoderConfigurationOptions;
import org.onvif.ver10.schema.VideoResolution;
import org.onvif.ver10.schema.VideoRateControl;
import org.onvif.ver10.schema.VideoSource;
import org.onvif.ver10.schema.VideoOutput;
import org.onvif.ver10.schema.VideoEncoderConfiguration;
import org.onvif.ver10.schema.DeviceIOCapabilities;
import org.onvif.ver10.schema.ObjectFactory;

import de.onvif.soap.OnvifDevice;
import de.onvif.soap.devices.InitialDevices;
import de.onvif.soap.devices.PtzDevices;
import de.onvif.soap.devices.ImagingDevices;
import de.onvif.soap.devices.MediaDevices;

public class Main {

	private static final String INFO = "Commands:\n  \n  url: Get snapshort URL.\n  info: Get information about each valid command.\n  exit: Exit this application.";

	public static void main(String args[]) {
		
		//if (args.length != 7) {
		if (args.length != 6) {
			System.out.println("Usage: Main.java IPadress username password pan tilt zoom");
			System.out.println("pan, tilt: -1.0 to 1.0");
			System.out.println("zoom: 0.0 to 1.0");
			return;
		}
		
		InputStreamReader inputStream = new InputStreamReader(System.in);
		BufferedReader keyboardInput = new BufferedReader(inputStream);
		String input, cameraAddress, user, password;

//		try {
			//System.out.println("Please enter camera IP (with port if not 80):");
			cameraAddress = args[0];//"172.16.1.[130,134,140]";
			//cameraAddress = "172.16.1.130";
			//System.out.println("Please enter camera username:");
			user = args[1];//"onvif";
			//user = "onvif";
			//System.out.println("Please enter camera password:");
			password = args[2];//"33IoxkAwZ4R";
			//password = "33IoxkAwZ4R";
			
			float x = Float.parseFloat(args[3]);//1.0f;
			float y = Float.parseFloat(args[4]);//1.0f;
			float zoom = Float.parseFloat(args[5]);//0.5f;
			//int fpsLimit = Integer.parseInt(args[6]);//1 int;
			int fpsLimit = 1;
			
			//float x = 1.0f;
			//float y = 1.0f;
			//float zoom = 1.0f;
//		}
/*		catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
*/
		//System.out.println("Connect to camera, please wait ...");
		OnvifDevice cam;
		try {
			cam = new OnvifDevice(cameraAddress, user, password);
		}
		catch (ConnectException | SOAPException e1) {
			System.err.println("No connection to camera, please try again.");
			return;
		}
		//System.out.println("Connection to camera successful!");

//		while (true) {
			try {
//				System.out.println();
//				System.out.println("Enter a command (type \"info\" to get commands):");
//				input = keyboardInput.readLine();
				
				
				input = "ptz";
				
				switch (input) {
				case "url": {
					List<Profile> profiles = cam.getDevices().getProfiles();
					for (Profile p : profiles) {
						System.out.println("URL von Profil \'" + p.getName() + "\': " + cam.getMedia().getSnapshotUri(p.getToken()));
					}
					break;
				}
				case "profiles":
					List<Profile> profiles = cam.getDevices().getProfiles();
					System.out.println("Number of profiles: " + profiles.size());
					break;
				case "info":
					System.out.println(INFO);
					break;
				case "ptz":
					List<Profile> prof = cam.getDevices().getProfiles();
					String profileToken = prof.get(0).getToken();
					PtzDevices ptzDevices = cam.getPtz();

					FloatRange panRange = ptzDevices.getPanSpaces(profileToken);
					FloatRange tiltRange = ptzDevices.getTiltSpaces(profileToken);
					FloatRange zoomRange = ptzDevices.getZoomSpaces(profileToken);
					
					float x_center = (panRange.getMax() + panRange.getMin()) / 2f;
					float y_center = (tiltRange.getMax() + tiltRange.getMin()) / 2f;
					
					float x_scale = (panRange.getMax() - panRange.getMin()) / 2f;
					float y_scale = (tiltRange.getMax() - tiltRange.getMin()) / 2f;
					float zoom_scale = (zoomRange.getMax() - zoomRange.getMin()) / 2f;
					
					float setx = x * x_scale + x_center;
					float sety = y * y_scale + y_center;
					float setzoom = zoom * zoom_scale + zoomRange.getMin();
					
					//System.out.println("pan max, min: "+ panRange.getMax() + ", "+ panRange.getMin());
					//System.out.println("tilt max, min: "+ tiltRange.getMax() + ", "+ tiltRange.getMin());
					//System.out.println("zoom max, min: "+ zoomRange.getMax() + ", "+ zoomRange.getMin());					
					
					//System.out.println("x center: " + x_center + ", y center: " + y_center + ", zoom scale "+ zoom_scale);
					System.out.println("x " + x + "  y " + y + "  zoom " + zoom);
					
					int fpsCheck = 0;
					/*
					for(Profile p : prof) {
						VideoEncoderConfiguration msettings = MediaDevices.getVideoEncoderConfiguration(p);
						VideoResolution res_m = msettings.getResolution();
						VideoRateControl vrc = msettings.getRateControl();
						System.out.println("fps limit (before): " + vrc.getFrameRateLimit());
						vrc.setFrameRateLimit(fpsLimit);
						//msettings.setResolution(res_m);
						msettings.setRateControl(vrc);
						//fpsCheck = vrc.getFrameRateLimit();
						//System.out.println("fps limit (after) " + vrc.getFrameRateLimit());
						p.setVideoEncoderConfiguration(msettings);
						
						prof.set(0, p);
					}
					System.out.println("fps limit (after): " + fpsCheck);
					*/
					
					if (ptzDevices.isAbsoluteMoveSupported(profileToken)) {
						if (ptzDevices.absoluteMove(profileToken, setx, sety, setzoom)) {
							System.out.println("Move completed.");
							//System.out.println("fps limit (after): " + fpsCheck);
							
						}
					}
					
					break;
				case "setting":
					List<Profile> prof_s = cam.getDevices().getProfiles();
					String profileToken_s = prof_s.get(0).getToken();
					
					ImagingDevices imgDevices = cam.getImaging();
					
					ImagingSettings20 settings = imgDevices.getImagingSettings(profileToken_s);
					
					break;
				case "media":
					InitialDevices init_m = cam.getDevices();
					List<Profile> prof_m = init_m.getProfiles();
					String profileToken_m = prof_m.get(0).getToken();
					
					/*
					MediaDevices mdDevices = cam.getMedia();
					VideoEncoderConfiguration msettings = MediaDevices.getVideoEncoderConfiguration(prof_m.get(0));
					VideoRateControl vrc = msettings.getRateControl();
					System.out.println("fps limit (before): " + vrc.getFrameRateLimit());
					vrc.setFrameRateLimit(fpsLimit);
					*/
					//DeviceIOCapabilities cap;

					//ObjectFactory fact = null;
					//VideoOutput aaa = fact.createVideoOutput();
					//VideoResolution res_m = aaa.getResolution();
					//System.out.println("x " + res_m.getWidth() + "  y " + res_m.getHeight());
					
					int fpsCheckMedia = 0;
					int resH =0;
					int resW=0;
					
					for(Profile p : prof_m) {
						//VideoEncoderConfiguration msettings =cam.getMedia().getVideoEncoderConfiguration(p);
						VideoEncoderConfiguration msettings = MediaDevices.getVideoEncoderConfiguration(p);
						//VideoResolution res_m = msettings.getResolution();
						VideoRateControl vrc = msettings.getRateControl();
						//vrc.setFrameRateLimit(1);
						//System.out.println("x " + res_m.getWidth() + "  y " + res_m.getHeight());
						System.out.println("fps limit (before): " + vrc.getFrameRateLimit());
						//res_m.setHeight(480);
						//resH = res_m.getHeight();
						//res_m.setWidth(640);
						//resW = res_m.getWidth();
						vrc.setFrameRateLimit(fpsLimit);
						//msettings.setResolution(res_m);
						fpsCheckMedia=vrc.getFrameRateLimit();
						msettings.setRateControl(vrc);
						System.out.println("fps limit (after) " + vrc.getFrameRateLimit());
						p.setVideoEncoderConfiguration(msettings);
						
						prof_m.set(0, p);
					}
					
					//System.out.println("fps limit (after) " + vrc.getFrameRateLimit());
					//System.out.println("fps limit (after): " + fpsCheckMedia);
					
					break;
/*
					H264Options h264 = msettings.getH264();
					List<VideoResolution> resol = h264.getResolutionsAvailable();

					for(VideoResolution r : resol) {
						System.out.println("x: " + r.getWidth() + "  y: " + r.getHeight());
					}
					
					IntRange frame = h264.getFrameRateRange();
					System.out.println("max framerate: " + frame.getMax() + "  min framerate: " + frame.getMin());
					
					JpegOptions jpeg = msettings.getJPEG();
					List<VideoResolution> resol_j = jpeg.getResolutionsAvailable();

					for(VideoResolution r : resol_j) {
						System.out.println("x: " + r.getWidth() + "  y: " + r.getHeight());
					}
					
					IntRange frame_j = jpeg.getFrameRateRange();
					System.out.println("max framerate: " + frame_j.getMax() + "  min framerate: " + frame_j.getMin());

					IntRange qual = msettings.getQualityRange();
					System.out.println("max quality: " + qual.getMax() + "  min quality: " + qual.getMin());
					
					break;
*/
				case "10":
					MediaDevices med = cam.getMedia();
					List<VideoSource> src = med.getVideoSources();
					VideoResolution res;
					for(VideoSource s : src) {
						res = s.getResolution();
						System.out.println("FR: " + s.getFramerate() + "  x: " + res.getWidth() + "  y: " + res.getHeight());
					}
					break;
				case "quit":
				case "exit":
				case "end":
					return;
				default:
					System.out.println("Unknown command!");
					System.out.println();
					System.out.println(INFO);
					break;
				}
			}
//			catch (IOException e) {
//				e.printStackTrace();
//			}
			catch (SOAPException e) {
				e.printStackTrace();
			}
//		}
	}
}