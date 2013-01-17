package com.viisi.droid.missedcallnotification.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.PendingIntent;
import android.telephony.SmsManager;

/**
 * Classe para corrigir bug do android de enviar o SMS 2x com o mesmo conteúdo.
 * Envia o SMS através de reflection.
 * 
 * Dependendo do aparelho ou da versão do android, este método que está sendo invocado via reflection não existe.
 * Neste caso enviar o SMS pela API padrão do android, mesmo com o bug de ser enviado 2x.
 * 
 * Link do bug:
 * 
 * @link http://code.google.com/p/android/issues/detail?id=26953
 *       Link da correção:
 * @link http://stackoverflow.com/questions/9706039/android-sendtextmessage-sends-two-identical-messages-on-exceution-how-to-fix
 * @author Tiago Emerick
 * @mail tiagoemerick@gmail.com
 * @since 24∕09∕2012
 * 
 */
public final class SendSMSBugCorrection {

	private SendSMSBugCorrection() {

	}

	public static void send(String phoneNumber, String message, PendingIntent sendIntent, PendingIntent deIntent, SmsManager smsManager) {
		List<PendingIntent> sendIntents = new ArrayList<PendingIntent>(1);
		sendIntents.add(sendIntent);

		List<PendingIntent> deIntents = new ArrayList<PendingIntent>(1);
		sendIntents.add(deIntent);

		@SuppressWarnings("rawtypes")
		Class aclass[] = new Class[9];

		aclass[0] = String.class;
		aclass[1] = String.class;
		aclass[2] = ArrayList.class;
		aclass[3] = ArrayList.class;
		aclass[4] = ArrayList.class;
		aclass[5] = Boolean.TYPE;
		aclass[6] = Integer.TYPE;
		aclass[7] = Integer.TYPE;
		aclass[8] = Integer.TYPE;

		Method method;

		try {
			method = smsManager.getClass().getMethod("sendMultipartTextMessage", aclass);

			Object aobj[] = new Object[9];
			aobj[0] = phoneNumber;
			aobj[1] = null;
			aobj[2] = smsManager.divideMessage(message);
			aobj[3] = sendIntents;
			aobj[4] = deIntents;
			aobj[5] = Boolean.valueOf(false);
			aobj[6] = Integer.valueOf(0);
			aobj[7] = Integer.valueOf(0);
			aobj[8] = Integer.valueOf(0);

			method.invoke(smsManager, aobj);

			// Any error, send using default android api with bug
		} catch (Exception e) {
			// doesnt work. there is a bug on android core. Its sending sms twice
			smsManager.sendTextMessage(phoneNumber, null, message, sendIntent, deIntent);
		}
	}

}
