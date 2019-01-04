package org.apache.cordova.plugin;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.DataOutputStream;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ShellExec extends CordovaPlugin {
    @Override
    public boolean execute(String action, JSONArray data, final CallbackContext callbackContext) throws JSONException {
        if (action.equals("exec")) {
            final String cmd = data.getString(0);

            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    ProcessBuilder builder = new ProcessBuilder();
                    builder.command("sh", "-c", cmd);
                    builder.redirectErrorStream(true); // important so that errors don't overrun the process' buffer and make it fail

                    int exitStatus = -1;
                    StringBuilder output = new StringBuilder();

                    try {
                        Process process = builder.start();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                        String line = "";
                        while ((line = reader.readLine()) != null) {
                            output.append(line + "\n");
                        }

                        exitStatus = process.waitFor();

                    } catch (IOException e) {
                        output.append("IOException: " + e.getMessage() + "\n");
                    } catch (InterruptedException e) {
                        output.append("InterruptedException: " + e.getMessage() + "\n");
                    } catch (Exception e) {
                        output.append("InterruptedException: " + e.getMessage() + "\n");
                    }
                    try {
                        JSONObject json = new JSONObject();
                        json.put("exitStatus", exitStatus);
                        json.put("output", output.toString());
                        callbackContext.success(json);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callbackContext.success();
                    }
                }
            });
            return true;
        }
        return false;
    }

}
