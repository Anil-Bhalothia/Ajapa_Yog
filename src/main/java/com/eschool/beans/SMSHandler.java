package com.eschool.beans;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SMSHandler {
	public void sendSMS(String pno,String otp)
	{
		System.out.println("Hello");
		try {
            String user = "AjapaYog";
            String pass = "123456";
            String sender = "ASTOVD";
            String phone = pno;
            String text = otp+" is your one time password for login Ajapa Yog Sansthan - Team Astrovedha";
            String priority = "ndnd";
            String stype = "normal";

            // URL encode the text parameter
            String encodedText = URLEncoder.encode(text, "UTF-8");

            // Construct the URL
            String urlString = "https://trans.smsfresh.co/api/sendmsg.php?" +
                    "user=" + user +
                    "&pass=" + pass +
                    "&sender=" + sender +
                    "&phone=" + phone +
                    "&text=" + encodedText +
                    "&priority=" + priority +
                    "&stype=" + stype;
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            System.out.println("Response: " + response.toString());
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	public void sendCustom(String pno,String message)
	{
		
		try {
            String user = "AjapaYog";
            String pass = "123456";
            String sender = "ASTOVD";
            String phone = pno;
            String text = message;
            String priority = "ndnd";
            String stype = "normal";

            // URL encode the text parameter
            String encodedText = URLEncoder.encode(text, "UTF-8");

            // Construct the URL
            String urlString = "https://trans.smsfresh.co/api/sendmsg.php?" +
                    "user=" + user +
                    "&pass=" + pass +
                    "&sender=" + sender +
                    "&phone=" + phone +
                    "&text=" + encodedText +
                    "&priority=" + priority +
                    "&stype=" + stype;
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            System.out.println("Response: " + response.toString());
            connection.disconnect();
        } catch (Exception e) {
           Logger log=LoggerFactory.getLogger(getClass());
           log.error("While Sending SMS"+e.getMessage());
        }
	}
	
}
