package root.cloudwalk.cn.root1300;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.EthernetManager;
import android.net.IpConfiguration;
import android.net.LinkAddress;
import android.net.NetworkUtils;
import android.net.ProxyInfo;
import android.net.StaticIpConfiguration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.execution.Command;

import java.net.InetAddress;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * ClassName:IPSetActivity <br/>
 * Description: TODO Description. <br/>
 * Date:     2018年03月09日 10:36 <br/>
 *
 * @author 284891377
 * @since JDK 1.7
 */
public class IPSetActivity extends Activity {


    @BindView(R.id.et_ip)
    EditText etIp;
    @BindView(R.id.et_mask)
    EditText etMask;
    @BindView(R.id.et_gw)
    EditText etGw;
    @BindView(R.id.et_dns1)
    EditText etDns1;
    @BindView(R.id.et_dns2)
    EditText etDns2;
    @BindView(R.id.set_shell_ip)
    Button setShellIp;
    @BindView(R.id.set_old_ip)
    Button setOldIp;
    @BindView(R.id.set_move_ip)
    Button setMoveIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip_set);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        int flag = extras.getInt("flag");
        if (flag == 10){
            setShellIp.setVisibility(View.VISIBLE);
            setOldIp.setVisibility(View.INVISIBLE);
        }else {
            setShellIp.setVisibility(View.INVISIBLE);
            setOldIp.setVisibility(View.VISIBLE);
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @OnClick({R.id.set_shell_ip, R.id.set_old_ip,R.id.set_move_ip})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.set_shell_ip:
                shellSetIP();
                break;
            case R.id.set_old_ip:
                //原始修改静态ip
                oldWayIP();
                break;
          case R.id.set_move_ip:
                //切换动态获取ip
                setMoveIp();
                break;
        }
    }

    private void oldWayIP() {
        setStaticIp();
    }

    private void shellSetIP() {
//        ShellUtils.execCommand("ifconfig eth0 "+et_ip.getText().toString()+" netmask "+et_mask.getText().toString()+" up",false);
//        //修改网关
//        ShellUtils.execCommand("route add default gw "+et_gw.getText().toString(),false);
//        //DNS1
//        ShellUtils.execCommand("setprop net.eth0.dns1 "+et_dns1.getText().toString(),false);
//        //DNS2
//        ShellUtils.execCommand("setprop net.eth0.dns2 "+et_dns2.getText().toString(),false);
//        // mac
//        ShellUtils.execCommand("ifconfig eth1 hw ether 00:11:00:00:11:22",false);


        //测试1、系统签名 执行命令报 java.io.IOException: Cannot run program "su": error=13, Permission denied
        //测试2、系统签名+system/app 执行命令报 java.io.IOException: Cannot run program "su": error=13, Permission denied
        String commandStrIP = "ifconfig eth0 " + etIp.getText().toString() + " netmask " + etMask.getText().toString() + " up";
        doShell(commandStrIP);

        String commandStrGW = "route add default gw " + etGw.getText().toString();
        doShell(commandStrGW);

        String commandStrDNS1 = "setprop net.eth0.dns1 " + etDns1.getText().toString();
        doShell(commandStrDNS1);

        String commandStrDNS2 = "setprop net.eth0.dns2 " + etDns2.getText().toString();
        doShell(commandStrDNS2);

        String commandStrMac = "ifconfig eth1 hw ether 00:11:00:00:11:22";
        doShell(commandStrMac);

    }

    private void doShell(final String commandStr) {
        Command command = new Command(0, commandStr) {

            @Override
            public void commandCompleted(int id, int exitCode) {
                // 命令执行完成后会调用此方法
                Log.e("123", "exitCode=" + exitCode);
            }

            @Override
            public void commandOutput(int id, String line) {
                // 命令执行的过程中会执行此方法，line参数可用于调试
                Log.e("123", "line=" + line);
            }

            @Override
            public void commandTerminated(int id, String reason) {
                // 命令被取消后的执行此方法
                Log.e("123", "reason=" + reason);
            }

        };
        try {
            RootTools.getShell(true).add(command);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //设置动态ip
    public void setMoveIp(){
        try {
            @SuppressLint("WrongConstant")
            EthernetManager mEthManager = (EthernetManager) getSystemService(Context.ETHERNET_SERVICE);
            Log.e("11111","mEthManager:"+mEthManager);
//            mEthManager = (EthernetManager) getSystemService("ethernet");
            //对比STATIC，只需要把StaticIpConfiguration赋值为null
            IpConfiguration config = new IpConfiguration(IpConfiguration.IpAssignment.DHCP, IpConfiguration.ProxySettings.NONE, null, ProxyInfo.buildDirectProxy(null,0));
            mEthManager.setConfiguration(config);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    //设置静态ip
    private void setStaticIp(){

        try {
            @SuppressLint("WrongConstant")
            EthernetManager mEthManager = (EthernetManager) getSystemService(Context.ETHERNET_SERVICE);
            Log.e("11111","mEthManager:"+mEthManager);
            //每个IpConfiguration对象内部都包含了一个StaticIpConfiguration对象，对于DHCP方式来说这个对象赋为null
            StaticIpConfiguration staticIpConfiguration = new StaticIpConfiguration();//用于保存静态IP、dns、gateway、netMask相关参数配置
            InetAddress mIpAddr = NetworkUtils.numericToInetAddress(etIp.getText().toString());//把192.168.1.1这种格式字符串转化为IP地址对象
            String[] strs = etMask.getText().toString().split("\\.");
            int count = 0;
            for(String str : strs){
                if(str.equals("255")){
                    count++;
                }
            }
            int prefixLength = count*8;
            LinkAddress mIpAddress = new LinkAddress(mIpAddr,prefixLength);//prefixLength就是表示子网掩码字符有几个255，比如255.255.255.0的prefixLength为3
            InetAddress mGateway = NetworkUtils.numericToInetAddress(etGw.getText().toString());//默认网关
            ArrayList<InetAddress> mDnsServers = new ArrayList<InetAddress>();//DNS
            mDnsServers.add(NetworkUtils.numericToInetAddress(etDns1.getText().toString()));
            mDnsServers.add(NetworkUtils.numericToInetAddress(etDns2.getText().toString()));

            staticIpConfiguration.ipAddress = mIpAddress;
            staticIpConfiguration.gateway = mGateway;
            staticIpConfiguration.dnsServers.addAll(mDnsServers);

            //ProxySettings为代理服务配置，主要有STATIC（手动代理）、PAC（自动代理）两种，NONE为不设置代理，UNASSIGNED为未配置代理（framework会使用NONE替代它）
            //ProxyInfo包含代理配置信息
            IpConfiguration config = new IpConfiguration(IpConfiguration.IpAssignment.STATIC, IpConfiguration.ProxySettings.NONE, staticIpConfiguration, ProxyInfo.buildDirectProxy(null,0));
            mEthManager.setConfiguration(config);//执行该方法后，系统会先通过EthernetConfigStore保存IP配置到data/misc/ethernet/ipconfig.txt，再更新以太网配置、通过EthernetNetworkFactory重启eth设备（最终通过NetworkManagementService来操作开启关闭设备、更新状态）
            //NetworkManagementService服务中提供了各种直接操作eth设备的API，如开关、列举、读写配置eth设备，都是通过发送指令实现与netd通信
            //Netd 就是Network Daemon 的缩写，表示Network守护进程，Netd负责跟一些涉及网络的配置，操作，管理，查询等相关的功能实现
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}
