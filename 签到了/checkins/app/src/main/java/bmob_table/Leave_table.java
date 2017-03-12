package bmob_table;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/10/27.
 */
public class Leave_table extends BmobObject {
    private String id ="0";
    private String account="0";
    private String realName="0";
    private String LeaveTime="0";
    private String LeaveType="0";
    private String BSSID;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getLeaveTime() {
        return LeaveTime;
    }

    public void setLeaveTime(String leaveTime) {LeaveTime = leaveTime;}

    public String getLeaveType() {
        return LeaveType;
    }

    public void setLeaveType(String leaveType) {LeaveType = leaveType;}

    public String getBSSID() {
        return BSSID;
    }

    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }
}
