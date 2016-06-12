package com.tangramfactory.smartweight.activity.device;

/**
 * Created by B on 2016-06-02.
 */
public class CmdConst {
    public final static byte REQUEST_CMD_SIZE            = 20;

    public final static byte CMD_REQUEST_UUID               = 1;
    public final static byte CMD_REQUEST_START              = 10;
    public final static byte CMD_REQUEST_BREAK              = 11;
    public final static byte CMD_REQUEST_STOP               = 12;


    public final static byte CMD_RESPONSE_BATTERY           = 4;
    public final static byte CMD_RESPONSE_COUNT_DATA        = 8;
    public final static byte CMD_RESPONSE_ANGLE_DATA        = 9;

    public final static byte CMD_RESPONSE_BREAKTIME_DATA    = 11;


    public final static byte CMD_DEVIDE_VERSION             = 0x02;
    public final static byte CMD_DEVIDE_NAME                = 0x03;
    public final static byte CMD_DEVIDE_BATTERY_INFO        = 0x04;
    public final static byte CMD_DEVIDE_SET_DEVICE_TIME     = 0x05;
    public final static byte CMD_DEVIDE_EXERCISE_INFO       = 0x06;
//    public final static int CMD_DEVIDE_    = 0x07;


    public final static int MASK_COMMAND                    =   0x7F;
    public final static int MASK_SEQUENCE_NUM               =   0xF8;
    public final static int MASK_COMMAND_LEN                =   0x07;


}
