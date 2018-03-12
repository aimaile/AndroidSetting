package root.cloudwalk.cn.root1300;

import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.execution.Command;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.button)
    Button button;
    @BindView(R.id.button2)
    Button button2;
    @BindView(R.id.button3)
    Button button3;
    @BindView(R.id.button4)
    Button button4;
    @BindView(R.id.set_shell_voice)
    Button setShellVoice;
    @BindView(R.id.set_old_voice)
    Button setOldVoice;
    @BindView(R.id.reset_shell_company)
    Button resetShellCompany;
    @BindView(R.id.reset_old_company)
    Button resetOldCompany;
    @BindView(R.id.reset_shell_software)
    Button resetShellSoftware;
    @BindView(R.id.reset_old_software)
    Button resetOldSoftware;
    @BindView(R.id.set_shell_time)
    Button setShellTime;
    @BindView(R.id.set_old_time)
    Button setOldTime;
    @BindView(R.id.set_shell_ip)
    Button setShellIp;
    @BindView(R.id.set_old_ip)
    Button setOldIp;
    @BindView(R.id.et_time)
    EditText etTime;
    @BindView(R.id.tv_flag)
    TextView tvFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        tvFlag.setText("111111222222");
    }


    protected void doShell(String commandStr) {
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

    public void reboot() {
//        IPowerManager power = IPowerManager.Stub.asInterface(ServiceManager.getService("power"));
//
//        if (power != null) {
//            try {
//                /**
//                 * 查看frameworks/base/core/java/android/os/IPowerManager.aidl
//                 * void shutdown(boolean confirm, boolean wait);
//                 * 所以，第一个是是否让用户确认，第二个是是否等待
//                 */
//                power.reboot(false, "tip", false);
//            } catch (RemoteException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }


        //测试1、有系统签名 验证可以用
        //测试2、系统签名+system/app 验证可以用
        PowerManager manager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        manager.reboot("重新启动系统");
    }

    @OnClick({R.id.button, R.id.button2, R.id.button3, R.id.button4, R.id.set_shell_voice, R.id.set_old_voice, R.id.reset_shell_company, R.id.reset_old_company, R.id.reset_shell_software, R.id.reset_old_software, R.id.set_shell_time, R.id.set_old_time, R.id.set_shell_ip, R.id.set_old_ip})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button:
                //静默安装
                //测试2、系统签名+system/app 报：java.io.IOException: Cannot run program "su": error=13, Permission denied
                doShell("pm install sdcard/demo.apk");
                break;
            case R.id.button2:
                //原生静默安装
                nativeInstallAPK("/sdcard/demo.apk");
                break;
            case R.id.button3:
                //重启设备
                shellReboot();
                break;
            case R.id.button4:
                //原生重启设备
                reboot();
                break;
            case R.id.set_shell_voice:
                //修改系统声音
                shellSetVoice();
                break;
            case R.id.set_old_voice:
                //原生修改系统声音
                nativeSetVoice(Integer.parseInt(etTime.getText().toString()));
                break;
            case R.id.reset_shell_company:
                //恢复出厂设置
                ShellRecoveryDevice();
                break;
            case R.id.reset_old_company:
                //原生恢复出厂设置
                nativeRecoveryDevice();
                break;
            case R.id.reset_shell_software:
                //重置软件  pm clear 清除应用数据
                shellResetSoftware();
                break;
            case R.id.reset_old_software:
                //原生重置软件
                nativeResetSoftware();
                break;
            case R.id.set_shell_time:
                //修改系统时间
                shellSetTime();
                break;
            case R.id.set_old_time:
                //原生修改系统时间
                try {
                    String dateTime="19920816000000";
                    Calendar c = Calendar.getInstance();
                    c.setTime(new SimpleDateFormat("yyyyMMddHHmmss").parse(dateTime));
                    System.out.println("时间转化后的毫秒数为："+c.getTimeInMillis());
                    nativeSetTime(c.getTimeInMillis());
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                break;
            case R.id.set_shell_ip:
                //修改以太网ip
                Intent intent = new Intent(this, IPSetActivity.class);
                //标识
                intent.putExtra("flag", 10);
                startActivity(intent);
                break;
            case R.id.set_old_ip:
                //原生修改以太网ip
                Intent intentOld = new Intent(this, IPSetActivity.class);
                intentOld.putExtra("flag", 11);
                startActivity(intentOld);
                break;
        }
    }

    private void nativeInstallAPK(String path) {
        //反射静默安装
        //测试1、系统签名 测试有效
        String sdCardPath = getSdCardPath();
        installSilentWithReflection(this, sdCardPath + "/demo.apk", "cn.cloudwalk.app.cwkepa");
    }

    private void nativeResetSoftware() {
        CacheUtil.clearAllCache(this);
        Toast.makeText(this, "缓存已清理", Toast.LENGTH_SHORT).show();
    }

    private void shellResetSoftware() {
        //测试1、系统签名 权限不够
        //测试2、系统签名+system/app 权限不够 报java.io.IOException: Cannot run program "su": error=13, Permission denied
        String cmd = "pm clear root.cloudwalk.cn.root1300";
        doShell(cmd);

    }

    private void nativeRecoveryDevice() {
        //声明广播接受者，申明<uses-permission android:name="android.permission.MASTER_CLEAR" />
        //测试1、系统签名  测试有效
        //测试2、系统签名+system/app目录  测试有效
        try {
            sendBroadcast(new Intent("android.intent.action.MASTER_CLEAR"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ShellRecoveryDevice() {
        // 测试1、系统签名 测试没用
        // 测试2、系统签名+system/app目录 测试没用
        try {
            // 同关机原理
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream out = new DataOutputStream(
                    process.getOutputStream());
            out.writeBytes("reboot recovery\n");
            out.writeBytes("exit\n");
            out.flush();
        } catch (IOException e) {
        }
    }

    private void nativeSetVoice(int nowVoice) {

        //有系统签名 声音设置  测试有效（正常情况也可以生效）
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        int max = audioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC );
//        int current = audioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, nowVoice, 0);


    }

    private void shellSetVoice() {
        //未找到实现指令

    }

    private void shellReboot() {
        //su -c reboot   // reboot -p
        //测试1、只是系统签名 还是缺权限
        //测试2、系统签名+system/app目录  报java.io.IOException: Cannot run program "su": error=13, Permission denied
        String cmd = "su -c reboot";
        doShell(cmd);

    }

    private void nativeSetTime(long time) {
        //测试1、系统签名，测试有效
        //测试2、系统签名，测试有效
        SystemClock.setCurrentTimeMillis(time);
    }

    private void shellSetTime() {
        //测试1、系统签名，执行su报错Permission denied
        //测试2、系统签名+system/app 执行su报错java.io.IOException: Cannot run program "su": error=13, Permission denied
        try {
            Process process = Runtime.getRuntime().exec("su");
            String datetime = "20131023.112800"; //测试的设置的时间【时间格式 yyyyMMdd.HHmmss】
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("setprop persist.sys.timezone GMT\n");
            os.writeBytes("/system/bin/date -s " + datetime + "\n");
            os.writeBytes("clock -w\n");
            os.writeBytes("exit\n");
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //反射静默安装
    //测试1、系统签名 测试有效
    public void installSilentWithReflection(Context context, String filePath, String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            Method method = packageManager.getClass().getDeclaredMethod("installPackage",
                    new Class[]{Uri.class, IPackageInstallObserver.class, int.class, String.class});
            method.setAccessible(true);
            File apkFile = new File(filePath);
            Uri apkUri = Uri.fromFile(apkFile);

            method.invoke(packageManager, new Object[]{apkUri, new IPackageInstallObserver.Stub() {
                @Override
                public void packageInstalled(String pkgName, int resultCode) throws RemoteException {
                    // resultCode= 1表示安装成功
                    Log.e("11111", "packageInstalled = " + pkgName + "; resultCode = " + resultCode);
                }
            }, Integer.valueOf(2), packageName});
            //PackageManager.INSTALL_REPLACE_EXISTING = 2;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取SD卡根目录路径
     *
     * @return
     */
    public static String getSdCardPath() {
        boolean exist = isSdCardExist();
        String sdpath = "";
        if (exist) {
            sdpath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath();
        } else {
            sdpath = "不适用";
        }
        return sdpath;

    }

    /**
     * 判断SDCard是否存在 [当没有外挂SD卡时，内置ROM也被识别为存在sd卡]
     *
     * @return
     */
    public static boolean isSdCardExist() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }


}
