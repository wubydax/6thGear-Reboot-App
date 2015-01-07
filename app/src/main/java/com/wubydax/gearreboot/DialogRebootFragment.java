package com.wubydax.gearreboot;

/*
 * Copyright (c) 2014 Anna Berkovitch
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PowerManager;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author anna
 */
class DialogRebootFragment extends DialogFragment {
PowerManager pm;




    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //Using PowerManager interface for reboot as system app
        pm = (PowerManager) getActivity().getSystemService(getActivity().POWER_SERVICE);

        //Creating Dialog Builder
        builder.setTitle(R.string.dialog_title)
                .setIcon(R.drawable.ic_reboot)

                .setSingleChoiceItems(R.array.reboot_items, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            //Managing onClick event of the OK button
            public void onClick(DialogInterface dialog, int which) {
                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                if (selectedPosition == 0) {
                    //Native android reboot
                    pm.reboot(null);
                } else if (selectedPosition == 1) {
                    //Hotboot requires "su" to run. Using Adam's Shell class
                    String command[] = {"su", "-c", "busybox killall system_server"};
                    Shell shell = new Shell();
                    shell.sendShellCommand(command);
                } else if (selectedPosition == 2) {
                    //Native android reboot with "reason" as "recovery
                    pm.reboot("recovery");

                } else if (selectedPosition == 3) {
                    //Native android reboot with "reason" as "download"
                    pm.reboot("download");
                } else if (selectedPosition == 4) {
                    //Using gearKill method to terminate SystemUI process
                    gearKill("com.android.systemui");
                }
            }


        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //exit bg activity upon negative button pressed
                getActivity().finish();
            }
        });
        Dialog dialog = builder.create();
        //dialog will be dismissed while tapped outside of it and will call an onDetach method and there close the bg activity
        dialog.setCanceledOnTouchOutside(true);
        return dialog;


    }

    @Override
    public void onDetach() {
        super.onDetach();
        //exit bg activity upon pressing back button while dialog is displayed
        getActivity().finish();
    }
    private void gearKill(String packageName) {

        Process su = null;

        // we need to get super user to use pkill
        try {

            su = Runtime.getRuntime().exec("su");

        } catch (IOException e) {

            e.printStackTrace();

        }

        // kill app with a package name specified in method calling
        if (su != null) {

            try {

                DataOutputStream os = new DataOutputStream(su.getOutputStream());
                os.writeBytes("pkill " + packageName + "\n");
                os.flush();
                os.writeBytes("exit\n");
                os.flush();
                su.waitFor();

            } catch (IOException e) {

                e.printStackTrace();

            } catch (InterruptedException e) {

                e.printStackTrace();

            }
        }
    }
}




