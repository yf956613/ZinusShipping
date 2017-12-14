package cn.zinus.warehouse.SocketConnect;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import cn.zinus.warehouse.Fragment.Event;
import cn.zinus.warehouse.util.Constant;

/**
 * Developer:Spring
 * DataTime :2017/9/13 11:58
 * Main Change:
 */

public class SyncPC implements Runnable {

    private Socket client;
    private ArrayList<?> list;
    private UpdateSqlite mUpdateSqlite;
    BufferedOutputStream out;
    BufferedInputStream in;

    public SyncPC(Socket client, ArrayList<?> list, Context mContext) {
        this.client = client;
        this.list = list;
        mUpdateSqlite = new UpdateSqlite(mContext);
    }

    @Override
    public void run() {

        try {
            //与pc端通信
            String currCMD = "";
            //输入输出流
            Log.e("warehouse", "开始监听11111");
            out = new BufferedOutputStream(client.getOutputStream());
            in = new BufferedInputStream(client.getInputStream());
            //开启一个循环
            while (true) {
                try {
                    if (!client.isConnected()) {
                        //连接不成功的时候跳出循环
                        Log.e("warehouse", "连接不成功的时候跳出循环");
                        break;
                    }
                    //连接成功

                    Log.e("warehouse", "一次连接监听");
                    currCMD = readCMDFromSocket(in);
                    String flag = currCMD.substring(0, Constant.SOCKETLENGTH);
                    String resultStr = currCMD.substring(Constant.SOCKETLENGTH);
                    Log.e("warehouse只有标志位+长度", flag + ":" + resultStr);
                    //currCMD是pc传过来的数据
                    if (flag.equals(Constant.UPDATEEXIT)) {
                        EventBus.getDefault().post(new Event.RefreshActivityEvent());
                        break;
                    } else if (flag.equals(Constant.UPLOADEXIT)) {
                        out.write(Constant.UPLOADEXIT.getBytes());
                        out.flush();
                        Log.e("结束上传", "结束");
                        break;
                    } else {
                        switch (flag) {

                            //region UpdateStockin
                            case Constant.UPDATESTOCKINSTART:
                                Log.e("kaishi", "881kaishi");
                                /**
                                 * 收到开始的标志以后,按顺序更新数据库表:
                                 * SF_INBOUNDORDER-->SF_CONSUMEINBOUND-->SF_CONSUMELOTINBOUND
                                 * 更完最后一个表传更新结束的标志
                                 */
                                out.write(Constant.SYNCSF_INBOUNDORDER.getBytes());
                                out.flush();
                                break;
                            case Constant.SYNCSF_INBOUNDORDER:
                                /**
                                 * 1:给pc发更新表的消息
                                 * 2:pc传标志位加长度
                                 * 3:给pc发消息(做好准备更新)
                                 * 4:pc传更新字符串
                                 * 5:更新
                                 * 6:给pc发更新下一个表的消息...
                                 */
                                int lengthSF_INBOUNDORDER = Integer.parseInt(resultStr);
                                out.write(Constant.IYNCSF_INBOUNDORDER.getBytes());
                                out.flush();
                                Log.e("更新SF_INBOUNDORDER", resultStr);
                                String strSF_INBOUNDORDER = receiveFileFromSocket(in, out, lengthSF_INBOUNDORDER);
                                Log.e("更新SF_INBOUNDORDER", strSF_INBOUNDORDER.length() + ":" + strSF_INBOUNDORDER);
                                mUpdateSqlite.updateInboundOrder(strSF_INBOUNDORDER);
                                Gson gson1 = new Gson();
                                String msg_SYNCSF_CONSUMEINBOUND = Constant.SYNCSF_CONSUMEINBOUND + gson1.toJson(list);
                                out.write(msg_SYNCSF_CONSUMEINBOUND.getBytes());
                                out.flush();
                                break;
                            case Constant.SYNCSF_CONSUMEINBOUND:
                                int lengthSF_CONSUMEINBOUND = Integer.parseInt(resultStr);
                                out.write(Constant.IYNCSF_CONSUMEINBOUND.getBytes());
                                out.flush();
                                Log.e("更新SF_CONSUMEINBOUND", resultStr);
                                String strSF_CONSUMEINBOUND = receiveFileFromSocket(in, out, lengthSF_CONSUMEINBOUND);
                                Log.e("更新SF_CONSUMEINBOUND", strSF_CONSUMEINBOUND.length() + ":" + strSF_CONSUMEINBOUND);
                                mUpdateSqlite.updateConsumeInbound(strSF_CONSUMEINBOUND);
                                out.write(Constant.SYNCSF_CONSUMELOTINBOUND.getBytes());
                                out.flush();
                                break;
                            case Constant.SYNCSF_CONSUMELOTINBOUND:
                                int lengthSF_CONSUMELOTINBOUND = Integer.parseInt(resultStr);
                                out.write(Constant.IYNCSF_CONSUMELOTINBOUND.getBytes());
                                out.flush();
                                Log.e("更新SF_CONSUMELOTINBOUND", resultStr);
                                String strSF_CONSUMELOTINBOUND = receiveFileFromSocket(in, out, lengthSF_CONSUMELOTINBOUND);
                                Log.e("更新SF_CONSUMELOTINBOUND", strSF_CONSUMELOTINBOUND.length() + ":" + strSF_CONSUMELOTINBOUND);
                                mUpdateSqlite.updateConsumeLotInbound(strSF_CONSUMELOTINBOUND);
                                out.write(Constant.UPDATEEXIT.getBytes());
                                out.flush();
                                break;
                            //endregion

                            //region UpdateStockout
                            case Constant.UPDATESTOCKOUTSTART:
                                /**
                                 * 收到开始的标志以后,按顺序更新数据库表:
                                 * SF_CONSUMEREQUEST-->SF_CONSUMEOUTBOUND-->SF_CONSUMELOTOUTBOUND
                                 * 更完最后一个表传更新结束的标志
                                 */
                                out.write(Constant.SYNCSF_CONSUMEREQUEST.getBytes());
                                out.flush();
                                break;
                            case Constant.SYNCSF_CONSUMEREQUEST:
                                int lengthSF_CONSUMEREQUEST = Integer.parseInt(resultStr);
                                out.write(Constant.IYNCSF_CONSUMEREQUEST.getBytes());
                                out.flush();
                                Log.e("更新SF_CONSUMEREQUEST", resultStr);
                                String strSF_CONSUMEREQUEST = receiveFileFromSocket(in, out, lengthSF_CONSUMEREQUEST);
                                Log.e("更新SF_CONSUMEREQUEST", strSF_CONSUMEREQUEST.length() + ":" + strSF_CONSUMEREQUEST);
                                mUpdateSqlite.updateConsumeRequest(strSF_CONSUMEREQUEST);
                                out.write(Constant.SYNCSF_CONSUMEOUTBOUND.getBytes());
                                out.flush();
                                break;
                            case Constant.SYNCSF_CONSUMEOUTBOUND:
                                int lengthSF_CONSUMEOUTBOUND = Integer.parseInt(resultStr);
                                out.write(Constant.IYNCSF_CONSUMEOUTBOUND.getBytes());
                                out.flush();
                                Log.e("更新SF_CONSUMEOUTBOUND", resultStr);
                                String strSF_CONSUMEOUTBOUND = receiveFileFromSocket(in, out, lengthSF_CONSUMEOUTBOUND);
                                Log.e("更新SF_CONSUMEOUTBOUND", strSF_CONSUMEOUTBOUND.length() + ":" + strSF_CONSUMEOUTBOUND);
                                mUpdateSqlite.updateConsumeOutbound(strSF_CONSUMEOUTBOUND);
                                out.write(Constant.SYNCSF_CONSUMELOTOUTBOUND.getBytes());
                                out.flush();
                                break;
                            case Constant.SYNCSF_CONSUMELOTOUTBOUND:
                                int lengthSF_CONSUMELOTOUTBOUND = Integer.parseInt(resultStr);
                                out.write(Constant.IYNCSF_CONSUMELOTOUTBOUND.getBytes());
                                out.flush();
                                Log.e("更新SF_CONSUMELOTOUTBOUND", resultStr);
                                String strSF_CONSUMELOTOUTBOUND = receiveFileFromSocket(in, out, lengthSF_CONSUMELOTOUTBOUND);
                                Log.e("更新SF_CONSUMELOTOUTBOUND", strSF_CONSUMELOTOUTBOUND.length() + ":" + strSF_CONSUMELOTOUTBOUND);
                                mUpdateSqlite.updateConsumeLotOutbound(strSF_CONSUMELOTOUTBOUND);
                                out.write(Constant.UPDATEEXIT.getBytes());
                                out.flush();
                                break;
                            //endregion

                            //region UpdateStockcheck
                            case Constant.UPDATESTOCKCHECKSTART:
                                /**
                                 * 收到开始的标志以后,按顺序更新数据库表:
                                 * SYNCSF_STOCKCHECK-->SYNCSF_STOCKCHECKDETAIL-->SYNCSF_STOCKLOTCHECKDETAIL
                                 * 更完最后一个表传更新结束的标志
                                 */
                                out.write(Constant.SYNCSF_STOCKCHECK.getBytes());
                                out.flush();
                                break;
                            case Constant.SYNCSF_STOCKCHECK:
                                int lengthSF_STOCKCHECK = Integer.parseInt(resultStr);
                                out.write(Constant.IYNCSF_STOCKCHECK.getBytes());
                                out.flush();
                                Log.e("更新SF_STOCKCHECK", resultStr);
                                String strSF_STOCKCHECK = receiveFileFromSocket(in, out, lengthSF_STOCKCHECK);
                                Log.e("更新SF_STOCKCHECK", strSF_STOCKCHECK.length() + ":" + strSF_STOCKCHECK);
                                mUpdateSqlite.updateStockCheck(strSF_STOCKCHECK);
                                out.write(Constant.SYNCSF_STOCKCHECKDETAIL.getBytes());
                                out.flush();
                                break;
                            case Constant.SYNCSF_STOCKCHECKDETAIL:
                                int lengthSF_STOCKCHECKDETAIL = Integer.parseInt(resultStr);
                                out.write(Constant.IYNCSF_STOCKCHECKDETAIL.getBytes());
                                out.flush();
                                Log.e("更新SF_STOCKCHECKDETAIL", resultStr);
                                String strSF_STOCKCHECKDETAIL = receiveFileFromSocket(in, out, lengthSF_STOCKCHECKDETAIL);
                                Log.e("更新SF_STOCKCHECKDETAIL", strSF_STOCKCHECKDETAIL.length() + ":" + strSF_STOCKCHECKDETAIL);
                                mUpdateSqlite.updateStockCheckDetail(strSF_STOCKCHECKDETAIL);
                                out.write(Constant.SYNCSF_STOCKLOTCHECKDETAIL.getBytes());
                                out.flush();
                                break;
                            case Constant.SYNCSF_STOCKLOTCHECKDETAIL:
                                int lengthSF_STOCKLOTCHECKDETAIL = Integer.parseInt(resultStr);
                                out.write(Constant.IYNCSF_STOCKLOTCHECKDETAIL.getBytes());
                                out.flush();
                                Log.e("更新STOCKLOTCHECKDETAIL", resultStr);
                                String strSF_STOCKLOTCHECKDETAIL = receiveFileFromSocket(in, out, lengthSF_STOCKLOTCHECKDETAIL);
                                Log.e("更新STOCKLOTCHECKDETAIL", strSF_STOCKLOTCHECKDETAIL.length() + ":" + strSF_STOCKLOTCHECKDETAIL);
                                mUpdateSqlite.updateStockLotCheckDetail(strSF_STOCKLOTCHECKDETAIL);
                                out.write(Constant.UPDATEEXIT.getBytes());
                                out.flush();
                                break;
//                            case Constant.SYNCSF_CONSUMESTOCK:
//                                int lengthSF_CONSUMESTOCK = Integer.parseInt(resultStr);
//                                out.write(Constant.IYNCSF_CONSUMESTOCK.getBytes());
//                                out.flush();
//                                Log.e("更新SF_CONSUMESTOCK", resultStr);
//                                String strSF_CONSUMESTOCK = receiveFileFromSocket(in, out, lengthSF_CONSUMESTOCK);
//                                Log.e("更新SF_CONSUMESTOCK", strSF_CONSUMESTOCK.length() + ":" + strSF_CONSUMESTOCK);
//                                mUpdateSqlite.updateConsumeStock(strSF_CONSUMESTOCK);
//                                out.write(Constant.SYNCSF_CONSUMABLELOT.getBytes());
//                                out.flush();
//                                break;
//                            case Constant.SYNCSF_CONSUMABLELOT:
//                                int lengthSF_CONSUMABLELOT = Integer.parseInt(resultStr);
//                                out.write(Constant.IYNCSF_CONSUMABLELOT.getBytes());
//                                out.flush();
//                                Log.e("更新SF_CONSUMABLELOT", resultStr);
//                                String strSF_CONSUMABLELOT = receiveFileFromSocket(in, out, lengthSF_CONSUMABLELOT);
//                                Log.e("更新SF_CONSUMABLELOT", strSF_CONSUMABLELOT.length() + ":" + strSF_CONSUMABLELOT);
//                                mUpdateSqlite.updateConsumableLot(strSF_CONSUMABLELOT);
//                                out.write(Constant.UPDATEEXIT.getBytes());
//                                out.flush();
//                                break;
                            //endregion

                            //region UpdateCommon
                            /**
                             * 收到开始的标志以后,按顺序更新数据库表:
                             * SF_CONSUMABLEDEFINITION-->SF_AREA-->SF_WAREHOUSE-->SF_CODE
                             * 更完最后一个表传更新结束的标志
                             */
                            case Constant.UPDATECOMMONSTART:
                                out.write(Constant.SYNCSF_CONSUMABLEDEFINITION.getBytes());
                                out.flush();
                                break;
                            case Constant.SYNCSF_CODE:
                                //Code表作为最后一个表格更新完所有sqlite的内容之后,告诉pc可以断开连接了
                                int lengthSF_CODE = Integer.parseInt(resultStr);
                                out.write(Constant.IYNCSF_CODE.getBytes());
                                out.flush();
                                Log.e("更新SF_CODE", resultStr);
                                String strSF_CODE = receiveFileFromSocket(in, out, lengthSF_CODE);
                                Log.e("更新SF_CODE", strSF_CODE.length() + ":" + strSF_CODE);
                                mUpdateSqlite.updateCode(strSF_CODE);
                                out.write(Constant.UPDATEEXIT.getBytes());
                                out.flush();
                                break;
                            case Constant.SYNCSF_CONSUMABLEDEFINITION:
                                int lengthSF_CONSUMABLEDEFINITION = Integer.parseInt(resultStr);
                                out.write(Constant.IYNCSF_CONSUMABLEDEFINITION.getBytes());
                                out.flush();
                                Log.e("更新CONSUMABLEDEFINITION", resultStr);
                                String strSF_CONSUMABLEDEFINITION = receiveFileFromSocket(in, out, lengthSF_CONSUMABLEDEFINITION);
                                Log.e("更新CONSUMABLEDEFINITION", strSF_CONSUMABLEDEFINITION.length() + ":" + strSF_CONSUMABLEDEFINITION);
                                mUpdateSqlite.updateConsumabledefinition(strSF_CONSUMABLEDEFINITION);
                                out.write(Constant.SYNCSF_AREA.getBytes());
                                out.flush();
                                break;
                            case Constant.SYNCSF_AREA:
                                int lengthSF_AREA = Integer.parseInt(resultStr);
                                out.write(Constant.IYNCSF_AREA.getBytes());
                                out.flush();
                                Log.e("更新SF_AREA", resultStr);
                                String strSF_AREA = receiveFileFromSocket(in, out, lengthSF_AREA);
                                Log.e("更新SF_AREA", strSF_AREA.length() + ":" + strSF_AREA);
                                mUpdateSqlite.updateArea(strSF_AREA);
                                out.write(Constant.SYNCSF_WAREHOUSE.getBytes());
                                out.flush();
                                break;
                            case Constant.SYNCSF_WAREHOUSE:
                                int lengthSF_WAREHOUSE = Integer.parseInt(resultStr);
                                out.write(Constant.IYNCSF_WAREHOUSE.getBytes());
                                out.flush();
                                Log.e("更新SF_WAREHOUSE", resultStr);
                                String strSF_WAREHOUSE = receiveFileFromSocket(in, out, lengthSF_WAREHOUSE);
                                Log.e("更新SF_WAREHOUSE", strSF_WAREHOUSE.length() + ":" + strSF_WAREHOUSE);
                                mUpdateSqlite.updateWareHouse(strSF_WAREHOUSE);
                                out.write(Constant.SYNCSF_CODE.getBytes());
                                out.flush();
                                break;
                            //endregion

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (client != null) {
                    client.close();
                    Log.e("warehouse","关闭");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /* 读取命令 */
    public String readCMDFromSocket(InputStream in) {
        int MAX_BUFFER_BYTES = 1024 * 1024;
        String msg = "";
        byte[] tempbuffer = new byte[MAX_BUFFER_BYTES];
        try {
            int numReadedBytes = in.read(tempbuffer, 0, tempbuffer.length);
            msg = new String(tempbuffer, 0, numReadedBytes, "utf-8");
            tempbuffer = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msg;
    }

    public static String receiveFileFromSocket(InputStream in,
                                               OutputStream out, int length) {
        String returnstr = "";
        byte[] filebytes = null;// 文件数据
        try {
            int filelen = length;// 文件长度
//            String strtmp = "read file length ok:" + filelen;
//            out.write(strtmp.getBytes("utf-8"));
//            out.flush();

            filebytes = new byte[filelen];
            int pos = 0;
            int rcvLen = 0;
            while ((rcvLen = in.read(filebytes, pos, filelen - pos)) > 0) {
                pos += rcvLen;
            }
            Log.e("1111111", Thread.currentThread().getName()
                    + "---->" + "read file OK:file size=" + filebytes.length);

            returnstr = new String(filebytes,"UTF8");
            Log.e("read file长度",returnstr.length()+"");
//            out.write("read file ok".getBytes("utf-8"));
//            out.flush();
        } catch (Exception e) {
            Log.e("1111111", Thread.currentThread().getName()
                    + "---->" + "receiveFileFromSocket error");
            e.printStackTrace();
        }
        return returnstr;

    }

}