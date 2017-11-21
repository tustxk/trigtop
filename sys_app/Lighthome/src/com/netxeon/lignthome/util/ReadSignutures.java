package com.netxeon.lignthome.util;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
//该类就一方法，判断当前app的证书是否为自己的。
public class ReadSignutures {
	private static final String mPublickey = "OpenSSLRSAPublicKey{modulus=8966b4545e7304c52fa10e50dedf8d4b3cdfb9cc5a509e7399dc6359597387759b144f1fb95cb700307aaefa76984bd9006905944d234bb59023487f38bda0f2ec35efc99f0b90204241e2befa57217d7c7cf330db68a562fdebbe4b980e324b9ab47b4bee636750a173211c31e94ce115ce423e9bef11d9cffa2fedfe82a88992dc1823ffdbe62cca8f9b2f822fe4fc1fa3161e0b7862fb9b290a2bbb856f9608167632b725b4e1be8ef8e70049ffa689408deb2c3b8d81752abb50c85db754561896f389d50995ebac9a8ec73a70ffb016fc61d47112ae058346c812e5a718eb7ac94a41435888b5989bbb8f4a8bf407e60b88a9dcfec2f343262f3b387881,publicExponent=10001}";

	//此方法用于匹配证书
	public static boolean checkIt(Activity context) {
		boolean isValid = false;
		String publickey = null;
		//获得已安装的应用程序信息 。可以通过getPackageManager()方法获得。 
		PackageManager pckMan = context.getPackageManager();
		PackageInfo pi = null;
		try {
			//获取带签名的包的信息
			pi = pckMan.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
		} catch (NameNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			//Array of all signatures read from the package file.（包名）
			byte[] signature = pi.signatures[0].toByteArray();
			//创建一个新的certificatefactory实例提供请求的证书类型。
			CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
			//X509Certificate为一个x509的证书类，这提供了一个标准的方法来访问一个X.509证书的所有属性。
			X509Certificate cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(signature));
			publickey = cert.getPublicKey().toString();
			if (publickey.equals(mPublickey)) {
				isValid = true;
				Log.e("secure", "check package passed  !!!!!!!");
			} else {
				Log.e("secure", "invalid package, finish it !!!!!!!");
				isValid = false;
				context.finish();
			}

		} catch (CertificateException e) {
			e.printStackTrace();
		}

		return isValid;
	}

}
