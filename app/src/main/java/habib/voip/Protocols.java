package habib.voip;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import habib.voip.network.TCPSender;

public class Protocols {
    public static final byte GETLIST = 1, CALL = 2, ENDCALL = 3, LOGOUT = 4,
            ACCEPTCALL = 5, REJECTCALL = 6, ERROR = 7, BUSY = 8, CALLSENT = 9, PORTEXCHANGE = 10, IPEXCHANGE = 11;
    static AlertDialog dialog;

    /**
     * @param wrap
     */
    public static void HandlePackage(ByteBuffer wrap) {
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
            default:
                break;
        }
    }

    private static void HandleIpExchange(ByteBuffer wrap) {
        byte[] ipAddress = new byte[4];
        ByteBuffer buffer = allocateBuffer();
        ipAddress[0] = wrap.get();
        ipAddress[1] = wrap.get();
        ipAddress[2] = wrap.get();
        ipAddress[3] = wrap.get();
        try {
            Manager manager = Manager.getManager();
            manager.ConnectedIpAddress = InetAddress.getByAddress(ipAddress);
            DatagramPacket packet = new DatagramPacket(new byte[1], 1, Manager.getManager().ConnectedIpAddress, 55555);
            manager.getUdpSocket().send(packet);
            Log.i(Values.LogTag,"Protocols.PORTEXCHANGE");
            buffer.put(Protocols.PORTEXCHANGE).putInt(manager.getUdpSocket().getLocalPort());
            manager.getSocket().getOutputStream().write(buffer.array());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void HandlePortExchange(ByteBuffer wrap) {
        int port = wrap.getInt();
        Manager.getManager().ConnectedPort = port;
        Log.i(Values.LogTag, Manager.getManager().ConnectedIpAddress + ":" + port);
        MainActivity.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainActivity.getActivity().startActivity(new Intent(MainActivity.getActivity(), VoiceCallActivity.class));
            }
        });
    }

    public static ByteBuffer allocateBuffer(int size) {
        return ByteBuffer.allocate(size).order(ByteOrder.LITTLE_ENDIAN);
    }

    public static ByteBuffer allocateBuffer() {
        return ByteBuffer.allocate(1000).order(ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * @param wrap
     */
    private static void HandleCallSent(ByteBuffer wrap) {
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

    private static ByteBuffer GetExchangeData() {
        ByteBuffer buffer = allocateBuffer(5);
        buffer.put(Protocols.PORTEXCHANGE);
        try {
            Manager manager = Manager.getManager();
            DatagramPacket packet = new DatagramPacket(new byte[1], 1, InetAddress.getByName(Values.IP), Values.PORT);
            manager.getUdpSocket().send(packet);
            buffer.putInt(manager.getUdpSocket().getLocalPort());
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * @param wrap
     */
    private static void HandleBusy(ByteBuffer wrap) {

    }

    /**
     * @param wrap
     */
    private static void HandleError(ByteBuffer wrap) {

    }

    /**
     * @param wrap
     */
    private static void HandleRejectCall(ByteBuffer wrap) {
        if (dialog != null) {
            dialog.dismiss();
        }
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

    /**
     * @param wrap
     */
    private static void HandleAccept(ByteBuffer wrap) {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    /**
     * @param wrap
     */
    private static void HandleLogout(ByteBuffer wrap) {

    }

    /**
     * @param wrap
     */
    private static void HandleEndCall(ByteBuffer wrap) {
        Values.running = false;
    }

    /**
     * @param wrap
     */
    private static void HandleCall(ByteBuffer wrap) {
        int callerId = wrap.getInt();
        MainActivity.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.getActivity());
                builder.setTitle("Call request");
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
    private static ArrayList<Integer> HandleUserList(ByteBuffer wrap) {
        final Activity activity = MainActivity.getActivity();
        final ArrayList<Integer> userList = new ArrayList<Integer>();
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
}