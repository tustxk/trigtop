package com.netxeon.lignthome.util;
 
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

public class CpuManager {
	
//	CpuManager cpuManager;
//  public CpuManager getInstance(){
//	  if (cpuManager==null) {
//		cpuManager = new CpuManager();
//	}
//	return cpuManager;  
//  }
	
	
        // 获取CPU最大频率（单位KHZ）
     // "/system/bin/cat" 命令行
     // "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq" 存储最大频率的文件的路径
        public static String getMaxCpuFreq() {
                String result = "";
                ProcessBuilder cmd;
                try {
                        String[] args = { "/system/bin/cat",
                                        "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq" };
                        cmd = new ProcessBuilder(args);
                        Process process = cmd.start();//启动一个线程
                        InputStream in = process.getInputStream();
                        byte[] re = new byte[24];
                        while (in.read(re) != -1) {
                                result = result + new String(re);
                        }
                        in.close();
                } catch (IOException ex) {
                        ex.printStackTrace();
                        result = "N/A";
                }
                return result.trim();
        }
         // 获取CPU最小频率（单位KHZ）
        public static String getMinCpuFreq() {
                String result = "";
                ProcessBuilder cmd;
                try {
                        String[] args = { "/system/bin/cat",
                                        "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq" };
                        cmd = new ProcessBuilder(args);
                        Process process = cmd.start();
                        InputStream in = process.getInputStream();//读取系统数据
                        byte[] re = new byte[24];
                        while (in.read(re) != -1) {
                                result = result + new String(re);
                        }
                        in.close();
                } catch (IOException ex) {
                        ex.printStackTrace();
                        result = "N/A";
                }
                return result.trim();
        }
         // 实时获取CPU当前频率（单位KHZ）
        public static String getCurCpuFreq() {
                String result = "N/A";
                try {
                        FileReader fr = new FileReader(
                                        "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
                        BufferedReader br = new BufferedReader(fr);
                        String text = br.readLine();
                        result = text.trim();
                } catch (FileNotFoundException e) {
                        e.printStackTrace();
                } catch (IOException e) {
                        e.printStackTrace();
                }
                return result;
        }
        // 获取CPU名字
        public static String getCpuName() {
                try {
                        FileReader fr = new FileReader("/proc/cpuinfo");
                        BufferedReader br = new BufferedReader(fr);
                        String text = br.readLine();
                        String[] array = text.split(":\\s+", 2);
                        for (int i = 0; i < array.length; i++) {
                        }
                        return array[1];
                } catch (FileNotFoundException e) {
                        e.printStackTrace();
                } catch (IOException e) {
                        e.printStackTrace();
                }
                return null;
        }
        
        //cpu使用率
        public float getCpuUsage() {

    		try {
    			RandomAccessFile reade = new RandomAccessFile("/proc/stat", "r");
    			String load = reade.readLine();
    			String[] toks = load.split(" ");
    			long idle1 = Long.parseLong(toks[5]);
    			long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3])
    					+ Long.parseLong(toks[4]) + Long.parseLong(toks[6])
    					+ Long.parseLong(toks[7]) + Long.parseLong(toks[8]);
    			try {
    				Thread.sleep(360);
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    			reade.seek(0);
    			load = reade.readLine();
    			reade.close();
    			toks = load.split(" ");
    			long idle2 = Long.parseLong(toks[5]);
    			long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3])
    					+ Long.parseLong(toks[4]) + Long.parseLong(toks[6])
    					+ Long.parseLong(toks[7]) + Long.parseLong(toks[8]);
    			return (int) (100 * (cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1)));
    		} catch (IOException ex) {
    			ex.printStackTrace();
    		}
    		return 0;
    	}
          
        
}