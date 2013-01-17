package com.viisi.droid.missedcallnotification.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.annotation.SuppressLint;

@SuppressLint("SimpleDateFormat")
public class DataUtil {

	private static DataUtil instance = new DataUtil();

	public DataUtil() {
	}

	public static DataUtil getInstance() {
		return instance;
	}

	public String retornaDataHoraAtualFormatados() {
		StringBuilder sb = new StringBuilder();

		Date dataAtual = getDataAtual();
		sb.append(getDataFormatada(dataAtual));
		sb.append(" - ");
		sb.append(getHoraFormatada(dataAtual));

		return sb.toString();
	}

	/**
	 * Retorna a data atual
	 * 
	 * @param
	 * @return
	 */
	public Date getDataAtual() {
		return getCalendar().getTime();
	}

	/**
	 * Retorna a data atual formatada como dd/MM/yyyy.
	 * 
	 * @param data
	 * @return
	 */
	public String getDataFormatada(Date data) {
		String dataFormatada = null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		if (data != null) {
			dataFormatada = sdf.format(data);
		}

		return dataFormatada;
	}

	public String getDataFormatada(Date data, SimpleDateFormat format) {
		String dataFormatada = null;
		if (data != null) {
			dataFormatada = format.format(data);
		}
		return dataFormatada;
	}

	/**
	 * Retorna a hora formatada como HH:mm.
	 * 
	 * @param
	 * @return
	 */
	public static String getHoraFormatada(Date data) {
		String dataFormatada = null;

		if (data != null) {
			dataFormatada = new SimpleDateFormat("HH:mm").format(data);
		}

		return dataFormatada;
	}

	/**
	 * Recebe data(dd/MM/yyyy) e horario (HH:mm), retorna um Date
	 * 
	 * @param data
	 * @param horario
	 * @return
	 */
	public Date getDataComHorario(String data, String horario) {
		Calendar c = Calendar.getInstance();

		c.setTime(toDate(data));
		c.set(Calendar.HOUR_OF_DAY, getHoras(horario));
		c.set(Calendar.MINUTE, getMinutos(horario));

		return c.getTime();
	}

	/**
	 * Recebe um <code>java.lang.String</code> e transforma para um Date no
	 * formato (dd/MM/YYYY)
	 * 
	 * @param dataStr
	 * @return
	 */
	public static Date toDate(String dataStr) {
		Date data = null;
		try {
			if (dataStr != null && !dataStr.trim().equals("")) {
				SimpleDateFormat df = criarSimpleDateFormat();
				df.setLenient(false);
				data = df.parse(dataStr);
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Erro formatando data [" + dataStr + "].");
		}

		return data;
	}

	/**
	 * Recebe um Date e retorna a Descri��o do m�s em Portugu�s
	 * 
	 * @param data
	 * @return
	 */
	public String getMesPorExtenso(Date data) {
		return formatDate(data, "MMMM");
	}

	/**
	 * Recebe um Date e retorna a descri��o do dia em Portugu�s Ex.:
	 * 'Segunda-feira'
	 * 
	 * @param data
	 * @return
	 */
	public String getDiaPorExtenso(Date data) {
		return formatDate(data, "EEEE");
	}

	/**
	 * Retorna a data por extenso em Portugu�s utilizando o padr�o informado
	 * 
	 * @param data
	 * @param pattern
	 * @return
	 */
	public String formatDate(Date data, String pattern) {
		if (data == null)
			return "";
		return formatDate(data, pattern);
	}

	/**
	 * Adiciona dias a uma data
	 * 
	 * @param data
	 * @param dias
	 * @return
	 */
	public Date adicionarDias(Date data, int dias) {
		Calendar calendar = getCalendar();

		calendar.setTime(data);
		calendar.add(Calendar.DAY_OF_MONTH, dias);

		Date dataRetorno = calendar.getTime();

		return dataRetorno;
	}

	/**
	 * Adiciona meses a uma data
	 * 
	 * @param data
	 * @param dias
	 * @return
	 */
	public Date adicionarMeses(Date data, int meses) {
		Calendar calendar = getCalendar();

		calendar.setTime(data);
		calendar.add(Calendar.MONTH, meses);

		return calendar.getTime();
	}

	/**
	 * Adiciona anos a uma data
	 * 
	 * @param data
	 * @param dias
	 * @return
	 */
	public Date adicionarAnos(Date data, int anos) {
		Calendar calendar = getCalendar();

		calendar.setTime(data);
		calendar.add(Calendar.YEAR, anos);

		return calendar.getTime();
	}

	/**
	 * Adiciona semanas a uma data
	 * 
	 * @param data
	 * @param dias
	 * @return
	 */
	public Date adicionarSemanas(Date data, int semanas) {
		Calendar calendar = getCalendar();

		calendar.setTime(data);
		calendar.add(Calendar.WEEK_OF_MONTH, semanas);

		return calendar.getTime();
	}

	/**
	 * Adiciona minutos a uma data
	 * 
	 * @param data
	 * @param minutos
	 * @return
	 */
	public Date adicionarMinutos(Date data, int minutos) {
		Calendar calendar = getCalendar();

		calendar.setTime(data);
		calendar.add(Calendar.MINUTE, minutos);

		return calendar.getTime();
	}

	/**
	 * Verifica se a dataA � anterior � dataB sem considerar a hora;
	 * 
	 * @param dataA
	 * @param dataB
	 * @return
	 */
	public boolean isDataMenor(Date dataA, Date dataB) {
		return zeraCalendar(dataA).before(zeraCalendar(dataB));
	}

	/**
	 * Verifica se a dataA � posterior � dataB sem considerar a hora;
	 * 
	 * @param dataA
	 * @param dataB
	 * @return
	 */
	public boolean isDataMaior(Date dataA, Date dataB) {
		return zeraCalendar(dataA).after(zeraCalendar(dataB));
	}

	/**
	 * Verifica se a dataA � igual � dataB sem considerar a hora
	 * 
	 * @param dataA
	 * @param dataB
	 * @return
	 */
	public boolean isDataIgual(Date dataA, Date dataB) {
		return zeraCalendar(dataA).equals(zeraCalendar(dataB));
	}

	// Retorna a diferen�a em dias entre duas datas, mas � mais eficiente e
	// elegante do que o m�todo abaixo
	public int diferencaEmDiasEntreDuasDatas(Date dataInicial, Date dataFinal) {
		GregorianCalendar dataInicialCal = (GregorianCalendar) Calendar.getInstance();
		dataInicialCal.setTime(dataInicial);
		dataInicialCal = new GregorianCalendar(dataInicialCal.get(Calendar.YEAR), dataInicialCal.get(Calendar.MONTH), dataInicialCal.get(Calendar.DATE));

		GregorianCalendar dataFinalCal = (GregorianCalendar) Calendar.getInstance();
		dataFinalCal.setTime(dataFinal);
		dataFinalCal = new GregorianCalendar(dataFinalCal.get(Calendar.YEAR), dataFinalCal.get(Calendar.MONTH), dataFinalCal.get(Calendar.DATE));
		long diferenca = dataFinalCal.getTimeInMillis() - dataInicialCal.getTimeInMillis();
		long diasDiferenca = diferenca / (1000 * 60 * 60 * 24);
		return (int) diasDiferenca;
	}

	public int diferencaEmMinutosEntreDuasDatas(Date dataInicial, Date dataFinal) {
		GregorianCalendar dataInicialCal = (GregorianCalendar) Calendar.getInstance();
		dataInicialCal.setTime(dataInicial);
		dataInicialCal = new GregorianCalendar(dataInicialCal.get(Calendar.YEAR), dataInicialCal.get(Calendar.MONTH), dataInicialCal.get(Calendar.DATE), dataInicialCal.get(Calendar.HOUR), dataInicialCal.get(Calendar.MINUTE));

		GregorianCalendar dataFinalCal = (GregorianCalendar) Calendar.getInstance();
		dataFinalCal.setTime(dataFinal);
		dataFinalCal = new GregorianCalendar(dataFinalCal.get(Calendar.YEAR), dataFinalCal.get(Calendar.MONTH), dataFinalCal.get(Calendar.DATE), dataFinalCal.get(Calendar.HOUR), dataFinalCal.get(Calendar.MINUTE));
		long diferenca = dataFinalCal.getTimeInMillis() - dataInicialCal.getTimeInMillis();
		long minutosDiferenca = diferenca / (1000 * 60);
		return (int) minutosDiferenca;
	}

	/**
	 * Zera as horas e retorna somente a data.
	 * 
	 * @param data
	 * @return
	 */
	public Date zeraCalendar(Date data) {
		Calendar calendar = getCalendarInstance();
		calendar.setTime(data);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

	/**
	 * Retorna a data com o hora de 23:59:59
	 * 
	 * @return Date
	 */

	public Date getDataHoraMaximaDia(Date data) {
		Calendar calendar = getCalendarInstance();
		calendar.setTime(data);
		calendar.set(Calendar.MILLISECOND, 23);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		return calendar.getTime();
	}

	/**
	 * Recupera a data atual com a hora zerada
	 * 
	 * @return
	 */
	public Date getDataAtualHoraZero() {
		return zeraCalendar(getDataAtual());
	}

	private Calendar getCalendarInstance() {
		return Calendar.getInstance();
	}

	/**
	 * Retorna o ano
	 * 
	 * @param data
	 * @return
	 */
	public int getAno(Date data) {
		Calendar calendarioData = Calendar.getInstance();
		calendarioData.setTime(data);
		return calendarioData.get(Calendar.YEAR);
	}

	/**
	 * Retorna o Dia do M�s
	 * 
	 * @param data
	 * @return
	 */
	public String getDia(Date data) {
		Calendar calendarioData = Calendar.getInstance();
		calendarioData.setTime(data);

		return String.valueOf(calendarioData.get(Calendar.DAY_OF_MONTH));
	}

	/**
	 * Retorna o M�s
	 * 
	 * @param data
	 * @return
	 */
	public String getMes(Date data) {
		Calendar calendarioData = Calendar.getInstance();
		calendarioData.setTime(data);

		return String.valueOf(calendarioData.get(Calendar.MONTH));
	}

	private int getMinutos(String horarioString) {
		return Integer.parseInt(horarioString.substring(3, horarioString.length()));
	}

	private int getHoras(String horarioString) {
		return Integer.parseInt(horarioString.substring(0, 2));
	}

	private static SimpleDateFormat criarSimpleDateFormat() {
		return new SimpleDateFormat("dd/MM/yyyy");
	}

	private Calendar getCalendar() {
		return Calendar.getInstance();
	}

	/**
	 * @param date
	 * @return
	 */
	static public String BrasilianDate2String(Date date) {
		DateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
		String result = simpleDateFormat.format(date);
		return result;
	}

	/**
	 * @param string
	 * @return
	 */
	static public Date String2BrasilianDate(String string) {
		Date result = new Date();
		if (!string.equals("")) {
			if (!string.equals("31.12.9999")) {
				DateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
				try {
					result = simpleDateFormat.parse(string);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else {
				result = null;
			}
		}
		return result;
	}

	/**
	 * @param string
	 * @return
	 */
	static public Date String2BrasilianDateTraco(String string) {
		Date result = new Date();
		if (!string.equals("")) {
			if (!string.equals("9999-12-31")) {
				DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
				try {
					result = simpleDateFormat.parse(string);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else {
				result = null;
			}
		}
		return result;
	}

	static public Timestamp stringToTimestamp(String string) {
		DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss.SSSSSS");
		Date result = null;
		try {
			result = simpleDateFormat.parse(string);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Timestamp ts = new Timestamp(result.getTime());
		return ts;
	}

	/**
	 * 
	 * @param dt
	 * @return
	 */
	static public Date formatDate(String dt) {

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		Date data = null;
		try {
			data = sdf.parse(dt);

		} catch (ParseException e) {

			e.printStackTrace();
		}

		return data;
	}

	/**
	 * Recupera lista de Dates contendo os anos anteriores ao ano atual, de
	 * acordo com o parametro passado.
	 * 
	 * @author Guilherme Broock - BRQ Curitiba
	 * @param numberYears
	 * @return List<Date>
	 */
	static public List<Date> getBeforeYears(int numberYears) {

		List<Date> dates = new ArrayList<Date>();
		GregorianCalendar gc = new GregorianCalendar();
		dates.add(gc.getTime());

		for (int i = 0; i < numberYears; i++) {
			gc.add(GregorianCalendar.YEAR, -1);
			dates.add(gc.getTime());
		}
		return dates;
	}

	/**
	 * Recupera o ano de um Date.
	 * 
	 * @author Guilherme Broock - BRQ Curitiba
	 * @param date
	 * @return String
	 */
	static public String getYear(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy");
		String strYear = format.format(date);
		return strYear;
	}

	/**
	 * Devolve um Timestamp com nanossegundos.
	 * 
	 * Obs: Percis�o de milissegundo, i.e., a partir de milissegundos pode
	 * ocorrer erro (um timestamp posterior ter valor inferior).
	 * 
	 * @author F4955639 Jo�o Paulo Limberger
	 * @return java.sql.Timestamp - Timestamp com nanos preenchidos.
	 */
	public static java.sql.Timestamp getTimestampNanos() {
		java.sql.Timestamp ts = new java.sql.Timestamp(System.currentTimeMillis());
		ts.setNanos((int) (ts.getNanos() + (System.nanoTime() % 1000000)));

		return ts;
	}
}
