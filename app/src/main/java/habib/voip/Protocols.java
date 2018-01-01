package habib.voip;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.InvalidKeyException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import habib.voip.crypto.SecurityManager;
import habib.voip.network.TCPSender;

public class Protocols {
    public static final byte CONNECT = 0, GETLIST = 1, CALL = 2, ENDCALL = 3, LOGOUT = 4,
            ACCEPTCALL = 5, REJECTCALL = 6, ERROR = 7, BUSY = 8, CALLSENT = 9, PORTEXCHANGE = 10, IPEXCHANGE = 11,
            SESSIONKEY = 12;
    static AlertDialog dialog;
    Manager manager = Manager.getManager();

    public static ByteBuffer allocateBuffer(int size) {
        return ByteBuffer.allocate(size).order(ByteOrder.LITTLE_ENDIAN);
    }

    public static ByteBuffer allocateBuffer() {
        return ByteBuffer.allocate(1024 * 1024 * 4).order(ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * @param wrap
     */
    private static void HandleLogout(ByteBuffer wrap) {

    }

    public static String join(byte[] array) {
        if (array.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        int i;
        for (i = 0; i < array.length - 1; i++)
            sb.append(array[i]);
        return sb.toString();
    }

    /**
     * @param wrap
     */
    public void HandlePackage(ByteBuffer wrap) {
        switch (wrap.get()) {
            case Protocols.GETLIST:
                HandleUserList(wrap);
                break;
            case Protocols.CALL:
                HandleCall(wrap);
                break;
            case Protocols.ENDCALL:
                HandleEndCall(wrap);
                break;
            case Protocols.LOGOUT:
                HandleLogout(wrap);
                break;
            case Protocols.ACCEPTCALL:
                HandleAccept(wrap);
                break;
            case Protocols.REJECTCALL:
                HandleRejectCall(wrap);
                break;
            case Protocols.ERROR:
                HandleError(wrap);
                break;
            case Protocols.BUSY:
                HandleBusy(wrap);
                break;
            case Protocols.CALLSENT:
                HandleCallSent(wrap);
                break;
            case Protocols.PORTEXCHANGE:
                HandlePortExchange(wrap);
                break;
            case Protocols.IPEXCHANGE:
                HandleIpExchange(wrap);
                break;
            case Protocols.SESSIONKEY:
                HandleSessionKey(wrap);
                break;
            default:
                break;
        }
    }

    private void HandleSessionKey(ByteBuffer wrap) {
        int length = wrap.getInt();
        Values.SessionKey = new byte[length];
        wrap.get(Values.SessionKey);
        Log.i(Values.LogTag, "HandleSessionKey Key Length" + length);
        try {
            SecurityManager securityManager = Manager.getManager().getSecurityManager();
            Values.SessionKey = securityManager.DecryptWithPrivate(Values.SessionKey);
            Log.i(Values.LogTag, join(Values.SessionKey));
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(Values.LogTag, "SessionKey has been gotten" + Base64.encode(Values.SessionKey, Base64.DEFAULT));
    }

    private void HandleIpExchange(ByteBuffer wrap) {
        byte[] ipAddress = new byte[4];
        ipAddress[0] = wrap.get();
        ipAddress[1] = wrap.get();
        ipAddress[2] = wrap.get();
        ipAddress[3] = wrap.get();
        try {
            Manager manager = Manager.getManager();
            manager.ConnectedIpAddress = InetAddress.getByAddress(ipAddress);
            DatagramPacket packet = new DatagramPacket(new byte[1], 1, manager.ConnectedIpAddress, 55555);
            manager.getUdpSocket().send(packet);
            Log.i(Values.LogTag, "Protocols.PORTEXCHANGE");
            ByteBuffer buffer = allocateBuffer(Byte.SIZE + Integer.SIZE);
            buffer.put(Protocols.PORTEXCHANGE).putInt(manager.getUdpSocket().getLocalPort());
            new TCPSender().executeContent(buffer.array());
            dismissDialog();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void HandlePortExchange(ByteBuffer wrap) {
        int port = wrap.getInt();
        manager.ConnectedPort = port;
        Log.i(Values.LogTag, manager.ConnectedIpAddress + ":" + port);
        MainActivity.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(Values.LogTag, "Port Exchange successed");
                MainActivity.getActivity().startActivity(new Intent(MainActivity.getActivity(), VoiceCallActivity.class));
            }
        });
    }

    /**
     * @param wrap
     */
    private void HandleCallSent(ByteBuffer wrap) {
        MainActivity.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.getActivity());
                builder.setCancelable(false);
                builder.setView(new ProgressBar(MainActivity.getActivity()));
                dialog = builder.create();
                dialog.show();
            }
        });
    }

    /**
     * @param wrap
     */
    private void HandleBusy(ByteBuffer wrap) {
        dismissDialog();
        MainActivity.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.getActivity());
                builder.setTitle("The person you have called has another call at the moment. Please try again lather");
                builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
            }
        });
    }

    /**
     * @param wrap
     */
    private void HandleError(ByteBuffer wrap) {
        dismissDialog();
        Toast.makeText(MainActivity.getActivity(), "An error occured while calling user", Toast.LENGTH_SHORT).show();
    }

    /**
     * @param wrap
     */
    private void HandleRejectCall(ByteBuffer wrap) {
        dismissDialog();
        MainActivity.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.getActivity());
                builder.setTitle("Your call has been rejected.");
                builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
            }
        });
    }

    /**
     * @param wrap
     */
    private void HandleAccept(ByteBuffer wrap) {
    }

    /**
     * @param wrap
     */
    private void HandleEndCall(ByteBuffer wrap) {
        Values.running = false;
        if (manager.getActivity() instanceof VoiceCallActivity) {
            manager.getActivity().finish();
        }
    }

    /**
     * @param wrap
     */
    private void HandleCall(ByteBuffer wrap) {
        final int callerId = wrap.getInt();
        MainActivity.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.getActivity());
                builder.setTitle("Call request from: " + callerId);
                builder.setCancelable(false);
                builder.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new TCPSender().executeContent(new byte[]{Protocols.REJECTCALL});
                        dialogInterface.dismiss();
                    }
                });
                builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new TCPSender().executeContent(new byte[]{Protocols.ACCEPTCALL});
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
            }

        });
    }

    /**
     * @param wrap
     * @return
     */
    private ArrayList<Integer> HandleUserList(ByteBuffer wrap) {
        final Activity activity = MainActivity.getActivity();
        final ArrayList<Integer> userList = new ArrayList<>();
        int userCount = wrap.getInt();
        for (int i = 0; i < userCount; i++) {
            userList.add(wrap.getInt());
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView list = (ListView) activity.findViewById(R.id.userList);
                MainActivity.ARRAY_LIST.clear();
                MainActivity.ARRAY_LIST.addAll(userList);
                ArrayAdapter<?> adapter = (ArrayAdapter<?>) list.getAdapter();
                adapter.notifyDataSetChanged();
            }
        });
        return userList;
    }

    private void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}