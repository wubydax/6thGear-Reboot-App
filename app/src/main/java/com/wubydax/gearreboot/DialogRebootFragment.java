package com.wubydax.gearreboot;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PowerManager;
import android.widget.Toast;




import java.io.DataOutputStream;
import java.io.IOException;


class DialogRebootFragment extends DialogFragment {
PowerManager pm;




    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        pm = (PowerManager) getActivity().getSystemService(getActivity().POWER_SERVICE);

        builder.setTitle(R.string.dialog_title)
                .setIcon(R.drawable.ic_reboot)

                .setSingleChoiceItems(R.array.reboot_items, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                if (selectedPosition == 0) {
                    pm.reboot(null);
//                    String command[] = {"su", "-c", "reboot"};
//                    Shell shell = new Shell();
//                    shell.sendShellCommand(command);
                } else if (selectedPosition == 1) {

                    String command[] = {"su", "-c", "busybox killall system_server"};
                    Shell shell = new Shell();
                    shell.sendShellCommand(command);


                } else if (selectedPosition == 2) {
                    pm.reboot("recovery");

                } else if (selectedPosition == 3) {
                    pm.reboot("download");
//                    String command[] = {"su", "-c", "reboot download"};
//                    Shell shell = new Shell();
//                    shell.sendShellCommand(command);
                } else if (selectedPosition == 4) {
                    gearKill("com.android.systemui");
                }
            }


        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                getActivity().finish();
            }
        });
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        return dialog;


    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().finish();
    }
    private void gearKill(String packageName) {

        Process su = null;

        // get su privileges
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




