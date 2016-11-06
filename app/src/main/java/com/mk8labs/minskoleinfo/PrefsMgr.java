package com.mk8labs.minskoleinfo;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.mk8labs.minskoleinfo.Log;
import com.mk8labs.minskoleinfo.loader.EventNotifier;

public class PrefsMgr {

	// private final static String STUDENTLISTPFX= "studentlist";
	private final static String STUDENTPFX = "student";

	// prefixes for lists of pupil to calendar mappings
	static final String SKEMAKALENDERPFX = "skemakalender";
	static final String LEKTIEKALENDERPFX = "lektiekalender";
	public static final String NOSYNC = "NONE";

	// list prefixes
	final static String CLASSDESCRIPTION = "classdescription";
	final static String TEACHERDESCRIPTION = "teacherdescription";

	//
	private final static String LASTUPDATETIME = "lastupdatetime";
	private final static String NEXTUPDATETIME = "nextupdatetime";
	private final static String UPDATEHISTORY = "updatehistory";

	private static SharedPreferences getPrefs(Context ctx) {
		return PreferenceManager.getDefaultSharedPreferences(ctx);
	}

	private static String getPrefString(Context ctx, String name) {
		return getPrefs(ctx).getString(name, null);
	}

	private static void setPrefString(Context ctx, String name, String value) {
		getPrefs(ctx).edit().putString(name, value).apply();
	}

	private static int getInt(Context ctx, String name) {
		return getPrefs(ctx).getInt(name, -1);
	}

	private static long getLong(Context ctx, String name) {
		return getPrefs(ctx).getLong(name, -1);
	}

	private static void setLong(Context ctx, String name, long value) {
		getPrefs(ctx).edit().putLong(name, value).apply();
	}

	public static String getBaseURL(Context ctx) {
		return getPrefString(ctx, "baseurl");
	}

	public static String getUserId(Context ctx) {
		return getPrefString(ctx, "userid");
	}

	public static String getPassword(Context ctx) {
		return getPrefString(ctx, "password");
	}

	public static int getSyncHour(Context ctx) {

		String v = getPrefString(ctx, "auto_load_hour");
		if (null == v) {
			return -1;
		} else {
			int i = Integer.parseInt(v);
			return i;
		}
	}

	public static String getScheduleCalendarKey(String ID) {
		return SKEMAKALENDERPFX + ID;
	}

	public static String getScheduleCalendarID(Context ctx, String ID) {
		String t=getPrefString(ctx, PrefsMgr.SKEMAKALENDERPFX + ID);
		if (null==t) {
			return PrefsMgr.NOSYNC;
		} else {
			return t;
		}
	}
	public static String getDiaryCalendarKey(String ID) {
		return LEKTIEKALENDERPFX + ID;
	}

	public static String getDiaryCalendarID(Context ctx, String ID) {
		String t=getPrefString(ctx, PrefsMgr.LEKTIEKALENDERPFX + ID);
		if (null==t) {
			return PrefsMgr.NOSYNC;
		} else {
			return t;
		}
	}
	

	public static void dumpPrefs(Context ctx) {
		SharedPreferences prefs = getPrefs(ctx);
		Map<String, ?> m = prefs.getAll();
		Log.d(m.toString());
	}

	public static void clearPref(Context ctx, String id) {
		getPrefs(ctx).edit().remove(id).commit();
	}

	public static Date getLastUpdateTime(Context ctx) {

		long l = getLong(ctx, LASTUPDATETIME);
		if (-1 == l) {
			return null;
		} else {
			return new Date(l);
		}
	}

	public static void setLastUpdateTime(Context ctx) {
		GregorianCalendar g = new GregorianCalendar();
		setLong(ctx, LASTUPDATETIME, g.getTimeInMillis());
	}

	public static Date getNextUpdateTime(Context ctx) {

		long l = getLong(ctx, NEXTUPDATETIME);
		if (-1 == l) {
			return null;
		} else {
			return new Date(l);
		}
	}

	private static EventNotifier UpdateTimeNotifier = null;

	public static void setNextUpdateTimeNotifier(EventNotifier notif) {
		UpdateTimeNotifier = notif;
	}

	public static void setNextUpdateTime(Context ctx, long time) {
		setLong(ctx, NEXTUPDATETIME, time);
		if (null != UpdateTimeNotifier) {
			UpdateTimeNotifier.onCompletion(true);
		}
	}

	public static String getClassDescription(Context ctx, String key) {
		return getPrefString(ctx, CLASSDESCRIPTION + key);
	}

	private static String[] DEFAULTCLASSDESCRIPTIONKEYS = new String[] { "DAN",
			"MAT", "ENG", "BIL", "HIS", "IDR", "KLA", "KRI", "MUS", "N/T" };
	private static String[] DEFAULTCLASSDESCRIPTIONVALUES = new String[] {
			"Dansk", "Matematik", "Engelsk", "Billedkunst", "Historie",
			"Idræt", "Klassens time", "Kristendomskundskab", "Musik",
			"Natur/Teknik" };

	public static void setClassDescription(Context ctx, String key, String value) {
		if (key.equals(value)) {
			for (int i = 0; i < DEFAULTCLASSDESCRIPTIONKEYS.length; i++) {
				String s = DEFAULTCLASSDESCRIPTIONKEYS[i];
				if (s.equals(key)) {
					value = DEFAULTCLASSDESCRIPTIONVALUES[i];
				}
			}
		}
		setPrefString(ctx, CLASSDESCRIPTION + key, value);
	}

	public static void setTeacherDescription(Context ctx, String key,
			String value) {
		setPrefString(ctx, TEACHERDESCRIPTION + key, value);
	}

	public static String getTeacherDescription(Context ctx, String key) {
		return getPrefString(ctx, TEACHERDESCRIPTION + key);
	}

	public static void setUpdateHistory(Context ctx, String value) {
		setPrefString(ctx, UPDATEHISTORY, value);
	}

	public static String getUpdateHistory(Context ctx) {
		String s = getPrefString(ctx, UPDATEHISTORY);
		return (null == s) ? "" : s;
	}

	// TODO: use school list
	/*
	private static final String[] SCHOOLLIST = new String[] {
			"10 klassecenter Vesthimmerland:www.10-vesthimmerland.skoleintra.dk",
			"10. Klasse Centret:www.10.skoleintra.dk",
			"10. klasse Gribskov - Uddannelsescentret:www.10klassegribskov.dk",
			"10. klassecenter Frederikshavn:www.fredhavn10.skoleintra.dk",
			"10. klasseskolen Bornholm:www.10ks.skoleintra.dk",
			"10.klasse Campus Kæge:www.10klassekoege.skoleintra.dk",
			"10.Klasseskolen:www.ungdomscenter.skoleintra.dk",
			"10Solræd:www.10klassesolroed.skoleintra.dk",
			"4klæverskolen:www.4kloeverskolen.dk",
			"A. P. Mæller Skolen:www.apms.skoleintra.dk",
			"Agerbæk-Starup Skole:www.starup-skole.skoleintra.dk",
			"Agerskov Kristne Friskole:www.agerskovkristnefriskole.skoleintra.dk",
			"Albertslund Lille Skole:www.albertslundlilleskole.skoleintra.dk",
			"Albertslund Ungecenter:www.elementet.skoleintra.dk",
			"Aller Friskole:www.aller-friskole.skoleintra.dk",
			"Allindelille Friskole:www.allindelille-friskole.skoleintra.dk",
			"Allingæbroskolen:www.allingaabro.skoleintra.dk",
			"Alquds skole:www.alqudsskole.skoleintra.dk",
			"Alslev Skole:www.alslevskole.skoleintra.dk",
			"Amager Privatskole:www.amagerprivatskole.skoleintra.dk",
			"Amagerskolen:www.amagerskolen.skoleintra.dk",
			"Andreasskolen:www.andreasskolen.skoleintra.dk",
			"Anna Trolles Skole:www.annatrollesskole.dk",
			"Ans Skole:www.ansskole.skoleintra.dk",
			"Ansgarskolen:www.ansgarskolen.skoleintra.dk",
			"Antvorskov Skole:www.antvorskovskole.skoleintra.dk",
			"Arden Skole:www.ardenskole.skoleintra.dk",
			"Arenaskolen:www.arenaskolen.skoleintra.dk",
			"Arresæ Skole:www.arresoeskole.skoleintra.dk",
			"As Friskole:www.as-friskole.skoleintra.dk",
			"Astrup Skole:www.astrup-skole.skoleintra.dk",
			"Astrup Specialskole:www.astrupskole.skoleintra.dk",
			"Asaa Skole:www.asaa.skoleintra.dk",
			"Atheneskolen:www.atheneskolen.skoleintra.dk",
			"Atuarfik Jærgen Brænlund:www.ajb.skoleintra.dk",
			"Atuarfik Mathias Storch:www.ams.skoleintra.dk",
			"Aulum Byskole:www.aulumbyskole.skoleintra.dk",
			"B93 Akademiet:www.b93akademiet.skoleintra.dk",
			"Baggesenskolen:www.baggesenskolen.skoleintra.dk",
			"Bagsværd Friskole:www.bagsvaerdfriskole.skoleintra.dk",
			"Bagsværd Kostskole og Gymnasium:www.bagkost.skoleintra.dk",
			"Bagsværd Skole:www.bagsvaerdskole.skoleintra.dk",
			"Bakkegærdskolen:www.bakkegaardsskolen.skoleintra.dk",
			"Bakkehusene:www.bakkehusene.skoleintra.dk",
			"Bakkeskolen:www.bakkeskolen.skoleintra.dk",
			"Bakkevejens Skole:www.bakkevejensskole.skoleintra.dk",
			"Balle Friskole, Balle Musik- & Idrætsefterskole.:www.balle-efterskole.skoleintra.dk",
			"Ballerup Ny Skole:www.ballerupnyskole.skoleintra.dk",
			"Ballerup Privatskole:www.ballerupprivatskole.skoleintra.dk",
			"Balsmoseskolen:www.balsmoseskolen.skoleintra.dk",
			"Baunegærd:www.baunegaard.skoleintra.dk",
			"Bavnebakkeskolen:www.bavnebakkeskolen.skoleintra.dk",
			"Bavnehæj Skole:www.kh.skoleintra.dk",
			"Bavnehæjskolen:www.bavnehojskolen.skoleintra.dk",
			"Bedsted Skole:www.bedstedskole.skoleintra.dk",
			"Behandlingsstederne Sæbæk:www.soebaek.skoleintra.dk",
			"Birkeræd Privatskole:www.birkerodprivatskole.skoleintra.dk",
			"Birkhovedskolen:www.birkhovedskolen.skoleintra.dk",
			"Bislev skole:www.bislevskole.skoleintra.dk",
			"Bistrupskolen:www.bistrup.skoleintra.dk",
			"Bjergby - Mygdal Skole:www.bjergby-mygdalskole.skoleintra.dk",
			"Bjergmarkskolen:www.gl-bjergmarkskolen.skoleintra.dk",
			"Bjærnehæjskolen:www.bjoernehoej.skoleintra.dk",
			"Blovstræd Skole:www.blovstroedskole.skoleintra.dk",
			"Blæsenborgafdelingen:www.blaesenborgafdeling.skoleintra.dk",
			"BLæKILDE EFTERSKOLE:www.blaakilde-efterskole.skoleintra.dk",
			"Boesagerskolen:www.boesagerskolen.skoleintra.dk",
			"Bogense Skole:www.kongslundskolen.skoleintra.dk",
			"Bogæ Kostskole:www.bk.skoleintra.dk",
			"Boldesager Skole:www.boldesagerskole.skoleintra.dk",
			"Borgerskolen:www.borgerskolen.skoleintra.dk",
			"Bork Havn Efterskole:www.borkhavn.skoleintra.dk",
			"Bork Skole:www.bork-skole.skoleintra.dk",
			"Borris Skole:www.borris-skole.skoleintra.dk",
			"Borup Privatskole:www.borupprivatskole.skoleintra.dk",
			"Borupgærdskolen:www.gl-borupgaard.skoleintra.dk",
			"Brattingsborgskolen:www.brattingsborgskolen.skoleintra.dk",
			"Bregninge Bjergsted Friskole:www.bbfs.skoleintra.dk",
			"Brejning Efterskole:www.brejning.skoleintra.dk",
			"Bremdal Skole:www.bremdalskole.skoleintra.dk",
			"Broskolen:www.broskolen.skoleintra.dk",
			"Brovandeskolen:www.brovandeskolen.skoleintra.dk",
			"Brylle Skole:www.brylle-skole.dk",
			"Bryndum Skole:www.bryndumskole.skoleintra.dk",
			"Brændgærdskolen:www.braendgaardskolen.skoleintra.dk",
			"Brændagerskolen, Helhedstilbudet:www.broendager.skoleintra.dk",
			"Brærupskolen:www.broerupskolen.skoleintra.dk",
			"Brærup Skole:www.braarup-skole.skoleintra.dk",
			"Buddinge Skole:www.buddingeskole.skoleintra.dk",
			"Byens Skole:www.byens-skole.skoleintra.dk",
			"Byhaveskolen:www.byhaveskolen.skoleintra.dk",
			"Byvangskolen:www.byvangskolen.skoleintra.dk",
			"Bækkegærdsskolen:www.baekkegaardsskolen.skoleintra.dk",
			"Bægebjergskolen:www.vesteraabyskole.skoleintra.dk",
			"Bægeskovskolen:www.boegeskovskolen.skoleintra.dk",
			"Bærnehave og fritidstilbud - BSC2:www.fritidshjemmet.skoleintra.dk",
			"Bærneuniversitetet:www.borneuni.skoleintra.dk",
			"Bævling Friskole:www.boevling.skoleintra.dk",
			"Bæring Skole:www.baaringskole.skoleintra.dk",
			"C. la Cours Skole:www.lacours.skoleintra.dk",
			"Campusskolen Ringsted:www.campusskolen.skoleintra.dk",
			"Center for Dagtilbud og Skoler, Greve Kommune:www.greve.skoleintra.dk",
			"Center-10, Aarhus High School:www.center10-aarhus.skoleintra.dk",
			"Charlotteskolen:www.charlotte-skolen.skoleintra.dk",
			"Christianshavns Dættreskole:www.doettreskolen.skoleintra.dk",
			"CLAVIS sprog & kompetence:www.clavis.skoleintra.dk",
			"Cornelius Hansen Skolen:www.cornelius-hansen-skolen.skoleintra.dk",
			"CSU Egedammen:www.egedammen.skoleintra.dk",
			"Dagbehandlingsstedet Gedebjerg Skole:www.gedebjergskole.skoleintra.dk",
			"Daghæjskolen Sydvestjylland:www.dhskolen.skoleintra.dk",
			"Dalmose..Centralskole:www.dalmoseskole.skoleintra.dk",
			"Danehofskolen:www.borgeskovskolen.skoleintra.dk",
			"Danmarksgades Skole:www.danmarksgadesskole.skoleintra.dk",
			"Danmarksgades Skole:www.laerit.skoleintra.dk",
			"Den Alternative Skole:www.das-fgsk.skoleintra.dk",
			"Den lille skole:www.denlilleskole.skoleintra.dk",
			"Den Nye Friskole:www.dennyefriskole.skoleintra.dk",
			"Det Kgl. Vajsenhus:www.vajsenhuset.skoleintra.dk",
			"Distriktsskole Ganlæse:www.ds-ganloese.skoleintra.dk",
			"Djurslandsskolen:www.djurslandsskolen.skoleintra.dk",
			"Dronninglund Skole:www.dronninglund-skole.skoleintra.dk",
			"Duborg-Skolen:www.duborg-skolen.skoleintra.dk",
			"Durup Skole:www.durup-skole.skoleintra.dk",
			"Dybkær Specialskole:www.dybkaerspecialskole.skoleintra.dk",
			"Ebeltoft Skole:www.ebeltoftskole.skoleintra.dk",
			"Eckersberg Bærneunivers:www.eckersbergfriskole.skoleintra.dk",
			"Efterskolen DUI Leg og Virke Bisnapgærd:www.bisnap.skoleintra.dk",
			"Efterskolen Flyvesandet:www.flyvesandet.skoleintra.dk",
			"Efterskolen æstergærd:www.ostergaard.skoleintra.dk",
			"Egebakken:www.egebakken.skoleintra.dk",
			"Egebjergskolen:www.egebjergskolen.skoleintra.dk",
			"Egebækskolen:www.egebaekskolen.skoleintra.dk",
			"Egekratskolen:www.egekratskolen.skoleintra.dk",
			"Egelundskolen:www.egelundskolen.skoleintra.dk",
			"Egeskovskolen:www.egeskov.skoleintra.dk",
			"Egholmskolen:www.egholmskolen.skoleintra.dk",
			"Ejby Skole:www.ejbyskole.skoleintra.dk",
			"Ejderskolen:www.ejderskolen.skoleintra.dk",
			"Ellekildeskolen - skolen ved æen:www.ellekildeskolen.dk",
			"Ellidshæj Skole:www.ellidshoej-skole.skoleintra.dk",
			"Elling Skole:www.ellingskole.skoleintra.dk",
			"Elverdamsskolen:www.elverdamsskolen.skoleintra.dk",
			"Elverhæjens Skole:www.elverhoejens-skole.skoleintra.dk",
			"Engbjergskolen:www.engbjergskolen.dk",
			"Engdalskolen:www.engdalskolen.skoleintra.dk",
			"Enghaveskolen:www.enghaveskole.skoleintra.dk",
			"Enghaveskolen - Faaborg Friskole:faaborg-friskole.skoleintra.dk",
			"Engholmskolen:www.engholmskolen.dk",
			"Engskovskolen:www.engskovskolen.skoleintra.dk",
			"Erritsæ Fællesskole:www.erritsoe.skoleintra.dk",
			"Erslev Skole:www.erslevskole.skoleintra.dk",
			"Esbjerg Realskole:www.esbjergrealskole.skoleintra.dk",
			"Eskilstrup Skole:www.eskilstrupskole.skoleintra.dk",
			"Espergærde Skole:www.ny-espergaerde.skoleintra.dk",
			"Familiekurserne og Grupperne:www.grp-fkk.skoleintra.dk",
			"Farsæ Skole:www.farsoe-skole.skoleintra.dk",
			"Faster Skole:www.faster-skole.skoleintra.dk",
			"Favrdalen/Fjelstrup:www.fff.skoleintra.dk",
			"Faxe Kommunale Ungdomsskole:www.faxeungdomsskole.skoleintra.dk",
			"Feldborg Centralskole:www.feldborg-skole.skoleintra.dk",
			"Felsted Centralskole:www.felsted-skole.dk",
			"Femmæller Efterskole:www.femmoeller.skoleintra.dk",
			"Ferritslev Friskole:www.ferfri.skoleintra.dk",
			"Filskov Friskole:www.filskovfriskole.skoleintra.dk",
			"Finderuphæj Skole:www.finderuphoej.skoleintra.dk",
			"Firklæverskolen:www.firkloeverskolen.skoleintra.dk",
			"Firklæverskolen:www.firkloeverskolen-lejre.skoleintra.dk",
			"Firklæverskolen Give:www.fallesskolen.skoleintra.dk",
			"Fjelstervang Skole:www.fjelstervang-skole.skoleintra.dk",
			"Fjelsæ Friskole:www.fjelsoefriskole.skoleintra.dk",
			"Fjerritslev Skole:www.fjerritslev-skole.dk",
			"Fjordbakkeskolen:www.fjordbakkeskolen.skoleintra.dk",
			"Fjordskolen:www.fjordskolen-aabenraa.skoleintra.dk",
			"Fjordskolen - Dannelundeafdelingen:www.fjordskolen-dannelunde.dk",
			"Fladsæskolen:www.fladsaaskolen.skoleintra.dk",
			"Flakkebjerg Skole:www.flakkebjergskole.skoleintra.dk",
			"Flauenskjold Skole:www.flauenskjold.skoleintra.dk",
			"Flæng Skole:www.floengskole.skoleintra.dk",
			"Forberedelsesskolen:www.forberedelsesskolen.dk",
			"Forlev Friskole:www.forlevfriskole.dk",
			"Forlev Friskole:www.forlevfriskole.skoleintra.dk",
			"Forældreskolen Aarhus:www.foraeldreskolen.skoleintra.dk",
			"Fourfeldtskolen:www.fourfeldtskolen.skoleintra.dk",
			"Fredensborg Skole:www.fredensborgskole.dk",
			"Fredericia Realskole:www.f-realskole.skoleintra.dk",
			"Frederik Barfods Skole:www.fbs.skoleintra.dk",
			"Frederiks Skole, SFO og Klub:www.frederiks-skole.skoleintra.dk",
			"Frederiksberg Ny Skole:www.frederiksbergnyskole.skoleintra.dk",
			"Frederiksberg Skole:www.frb-skole.skoleintra.dk",
			"Frederiksborg Byskole:www.frederiksborgbyskole.skoleintra.dk",
			"Frederiksodde Skole:www.frederiksoddeskole.skoleintra.dk",
			"Frederikssund Private Realskole:www.fpr.skoleintra.dk",
			"Frederiksværk Skole:www.frederiksvaerkskole.skoleintra.dk",
			"Friskolen Bramming:www.friskolen.skoleintra.dk",
			"Friskolen City Odense:www.friskolencityodense.skoleintra.dk",
			"Friskolen i Hinnerup:www.friskolen-hinnerup.skoleintra.dk",
			"Friskolen i Skive:www.friskoleniskive.skoleintra.dk",
			"Friskolen æster Egesborg:www.egesborg.skoleintra.dk",
			"Friskolen æsterlund:www.friskolen-oesterlund.skoleintra.dk",
			"Frydenhæjskolen:www.frydenhoejskolen.skoleintra.dk",
			"Frydenstrandskolen:www.frydenstrandskolen.skoleintra.dk",
			"Fræbjerg-Orte Friskole:www.fof.skoleintra.dk",
			"Fællesinstitutionen Ebberup:www.ebberupskole.skoleintra.dk",
			"Fællesskolen Bevtoft-Over Jerstal:www.fbo.skoleintra.dk",
			"Fællesskolen Hammelev Sct. Severin:www.fhs.skoleintra.dk",
			"Fællesskolen Nustrup Sommersted:www.fns.skoleintra.dk",
			"Fællesskolen Starup-æsby:www.fso.skoleintra.dk",
			"Faaborg Sundskole:www.faaborg-sundskole.skoleintra.dk",
			"Faarevejle Fri- og Efterskole:www.ffeonline.skoleintra.dk",
			"Gadstrup Skole:www.gadstrupskole.skoleintra.dk",
			"Galten Friskole:www.galten-friskole.skoleintra.dk",
			"Gammeqarfik:www.gammeqarfik.skoleintra.dk",
			"Gandrup Skole:www.gandrupskole.skoleintra.dk",
			"Ganlæse Skole:www.ganloese-skole.skoleintra.dk",
			"Gedsted Skole:www.gedsted-skole.skoleintra.dk",
			"Geelsgærdskolen:www.gs.skoleintra.dk",
			"Gelsted Skole:www.gelstedskole.skoleintra.dk",
			"Genner Univers:www.gennerskole.skoleintra.dk",
			"Gilbjergskolen:www.gilbjergskolen.dk",
			"Gildbroskolen:www.gildbroskolen.skoleintra.dk",
			"Gimsing Skole:www.gimsingskole.skoleintra.dk",
			"Gislev friskole:www.gislevfriskole.skoleintra.dk",
			"Gjellerupskolen:www.gjellerupskolen.skoleintra.dk",
			"Gjerlev-Enslev Skole:www.gjerlev.skoleintra.dk",
			"Gjern Skole:www.gjernskole.skoleintra.dk",
			"Gladsaxe Privatskole:www.gladsaxeprivatskole.skoleintra.dk",
			"Gladsaxe skole:www.gladsaxeskole.dk",
			"Glamsbjerg Fri-og Efterskole:www.gfeskole.skoleintra.dk",
			"Glejbjerg Skole og Bærnecenter:www.glejbjergskole.dk",
			"Glostrup Skole:www.glostrupskole.skoleintra.dk",
			"Gludsted Friskole:www.gludstedfriskole.skoleintra.dk",
			"Gram Efterskole:www.gram-efterskole.skoleintra.dk",
			"Gram Skole:www.gramskole.skoleintra.dk",
			"Grauballe Skole:www.grauballe-skole.skoleintra.dk",
			"Grejs Friskole:www.grejsfriskole.dk",
			"Greve Privatskole:www.greve-privatskole.skoleintra.dk",
			"Grindsted Privatskole:www.grindsted-privatskole.skoleintra.dk",
			"Grindsted Skole:www.grindstedskole.skoleintra.dk",
			"Grændalsvængets Skole:www.groendalsvaengets-skole.skoleintra.dk",
			"Grændalsvængets Skole:www.gvs.kk.dk",
			"GRæNHæJSKOLEN:www.gronhoj.skoleintra.dk",
			"Grænnemose Skole:www.groennemose.skoleintra.dk",
			"Grænnevang Skole:www.gronnevangskole.skoleintra.dk",
			"Grænvangskolen:www.gronvangskolen.skoleintra.dk",
			"Græsten Skole:www.graastenskole.skoleintra.dk",
			"Gudenædalens Friskole:www.gufs.skoleintra.dk",
			"Gudenæskolen:www.gudenaaskolen.skoleintra.dk",
			"Gug Skole:www.gug-skole.skoleintra.dk",
			"Gummerup skole:www.gummerup-skole.skoleintra.dk",
			"Gungehusskolen:www.gungehusskolen.skoleintra.dk",
			"Gustav Johannsen-Skolen:www.gustav-johannsen-skolen.skoleintra.dk",
			"GXU:www.gxu.skoleintra.dk",
			"Gærum Skole:www.gaerumskole.skoleintra.dk",
			"Gærding Skole:www.goerdingskole.skoleintra.dk",
			"Gærlev Skole:www.goerlevskole.skoleintra.dk",
			"Gæsetærnskolen:www.gaasetaarnskolen.skoleintra.dk",
			"Haderslev Realskole:www.haderslevreal.skoleintra.dk",
			"Haderup Skole:www.haderupskole.skoleintra.dk",
			"Hald Ege Efterskole:www.hald-ege-efterskole.skoleintra.dk",
			"Haldum-Hinnerup Skolen:www.haldum-hinnerupskolen.skoleintra.dk",
			"Hals Skole:www.halsskole.skoleintra.dk",
			"Halstedhus Efterskole:www.halstedhus.skoleintra.dk",
			"Hammershæj Skole:www.hammershoejskole.skoleintra.dk",
			"Hammerum Fri- & Efterskole:www.hamme.skoleintra.dk",
			"Hampelandskolen:www.hampelandskolen.skoleintra.dk",
			"Hannæsskolen:www.hannaesskolen.skoleintra.dk",
			"Hans Ræmer Skolen - Afdeling Mosaik:www.mosaik.skoleintra.dk",
			"Hans Ræmer Skolen, afdeling Vestermarie:www.vestermarie.skoleintra.dk",
			"Hans Ræmer Skolen, afdeling Aaker:www.aaker.skoleintra.dk",
			"Hareskov skole:www.hareskovskole.skoleintra.dk",
			"Hareskovens Lilleskole:www.hareskovens-lilleskole.skoleintra.dk",
			"Harlæse Skole:www.harloese.skoleintra.dk",
			"Haslev Privatskole:www.haslevprivatskole.skoleintra.dk",
			"Havndal skole:www.havndalskole.skoleintra.dk",
			"HAVREHEDSKOLEN:www.havrehed-sk.skoleintra.dk",
			"Hedegærdenes skole:www.hedegaardenesskole.skoleintra.dk",
			"Hedegærdsskolen:www.hedegaardsskolen.skoleintra.dk",
			"Hedegaardsskolen:www.hedegaardsskolen9700.skoleintra.dk",
			"Hedehusene Skole:www.hedehusene.skoleintra.dk",
			"Hee Skole:www.hee-skole.skoleintra.dk",
			"Heldagsskolen:www.bornholmsheldagsskole.skoleintra.dk",
			"Heldagsskolen Bræby ApS (inst.nr. 313011):www.heldagsskolen-braaby.skoleintra.dk",
			"Heldagsskolen i Brænderslev Kommune:www.broend-heldag.skoleintra.dk",
			"Helhedsskolen Flemlæse:www.helhedsskolen.skoleintra.dk",
			"Helhedstilbuddet Skovmoseskolen:www.skovmoseskolen.skoleintra.dk",
			"Hellebækskolen:www.hellebaekskolen.skoleintra.dk",
			"Hellevad Bærneunivers:www.hellevadskole.skoleintra.dk",
			"Hellum Friskole:www.hellumfriskole.skoleintra.dk",
			"Helsinge Realskole:www.helsinge-realskole.skoleintra.dk",
			"Helsingær Lilleskole:www.lilleskolen.skoleintra.dk",
			"Helsingær Skole:www.helsingoerskole.skoleintra.dk",
			"Hem Bærnehave og Skole:www.hemskole.skoleintra.dk",
			"Henriette Hærlæcks Skole:www.hhskole.skoleintra.dk",
			"Herfælge Privatskole:www.herfoelge-privatskole.skoleintra.dk",
			"Herlufmagle Skole:www.herlufmagleskole.skoleintra.dk",
			"Herning Friskole:www.herningfriskole.skoleintra.dk",
			"Herningsholmskolen:www.herningsholmskolen.skoleintra.dk",
			"Herningvej Skole:www.herningvej-skole.skoleintra.dk",
			"Herstedvester Skole:www.herstedvesterskole.skoleintra.dk",
			"Herstedæster Skole:www.herstedosterskole.skoleintra.dk",
			"Hestlund Efterskole:www.hestlund.skoleintra.dk",
			"Hillerslev skole:www.hillerslev-skole.skoleintra.dk",
			"Hilleræd Vest Skolen:www.hillerodvestskolen.skoleintra.dk",
			"Hillerædsholmskolen:www.hillerodsholm.skoleintra.dk",
			"Hindholm Privatskole:www.hindholm.skoleintra.dk",
			"Hirtshals Skolecenter:www.hirtshalsskolecenter.skoleintra.dk",
			"Hjallerup Skole:www.hjallerupskole.skoleintra.dk",
			"Hjembæk Efterskole:www.hjef.skoleintra.dk",
			"Hjemly Friskole:www.hjemlyfriskole.skoleintra.dk",
			"HJERM SKOLE:www.hjerm-skole.skoleintra.dk",
			"Hjerting Skole:www.hjertingskole.skoleintra.dk",
			"Hjortholmskolen:www.hjortholmskolen.skoleintra.dk",
			"Hjærring Private Realskole:www.hpr.skoleintra.dk",
			"Hodsager Skole:www.hodsager-skole.skoleintra.dk",
			"Holbergskolen:www.holberg-skolen.skoleintra.dk",
			"Holluf Pile Skole:www.hollufpileskole.skoleintra.dk",
			"Holmegaardskolen:www.holmegaard.skoleintra.dk",
			"Holmegærdsskolen:www.holmegaards-skolen.skoleintra.dk",
			"Holmeæskolen:www.holmeaaskolen.skoleintra.dk",
			"Holstebro Ungdomsskole:www.hk-ung.skoleintra.dk",
			"Holte Hus Efterskole:www.holte-hus.skoleintra.dk",
			"Horne-Tistrup Skolerne:www.horne-tistrup.skoleintra.dk",
			"Hou Skole:www.houskole.skoleintra.dk",
			"Humble Skole:www.humbleskole.skoleintra.dk",
			"Humlebæk lille Skole:www.humlebaek-lilleskole.skoleintra.dk",
			"Hundested Skole:www.hundestedskole.skoleintra.dk",
			"Husum Danske Skole:www.husumdanskeskole.skoleintra.dk",
			"Hvidovre Ungdomsskole:www.usk-hvidovre.skoleintra.dk",
			"Hvilebjergskolen:www.hvilebjergskolen.skoleintra.dk",
			"Hæje Kolstrup Skole:www.hkolstrupskole.skoleintra.dk",
			"Hæjgærdskolen:www.hoejgaardskolen.skoleintra.dk",
			"Hæjmark Skole:www.hoejmarkskole.skoleintra.dk",
			"Hæjmarkskolen:www.hoejmarkskolen.skoleintra.dk",
			"Hæjslev Skoleææ:www.hoejslev-skole.skoleintra.dk",
			"Hæjvangskolen:www.hoejvangskolen.skoleintra.dk",
			"Hæng Privatskole:www.hoengprivatskole.skoleintra.dk",
			"Hærby Efterskole:www.hu-ungdom.skoleintra.dk",
			"Hærby-Dybvad Skole:www.hoerby-dybvadskole.dk",
			"Hærsholm Lille Skole:www.hlsk.skoleintra.dk",
			"Hærup Centralskole:www.hoerup-skole.skoleintra.dk",
			"Hæsterkæb Skole:www.hoesterkoeb.skoleintra.dk",
			"Haahrs Skole:www.haahrs.skoleintra.dk",
			"Ida Holsts Skole:www.idaholst.skoleintra.dk",
			"Idestrup Privatskole:www.idestrup-privatskole.skoleintra.dk",
			"Idrætsefterskolen Klintsægaard:www.klintsoegaard.skoleintra.dk",
			"Idrætsefterskolen Lægærden:www.optur.skoleintra.dk",
			"Ikast æstre Skole:www.ikastoestreskole.skoleintra.dk",
			"Ikast-Brande Ungdomscenter:www.ikast10klasse.skoleintra.dk",
			"Institut Sankt Joseph:www.isj.skoleintra.dk",
			"Isefjordskolen:www.isefjordskolen.skoleintra.dk",
			"Ishæj Skole - SFO:www.ishojskole.skoleintra.dk",
			"Ishæjgærd:www.ishojgaard.skoleintra.dk",
			"ISI Idrætsefterskole:www.isi.skoleintra.dk",
			"Islev Skole:www.islevskole.skoleintra.dk",
			"Issæ-skolen:www.issoe-skolen.dk",
			"Janderup Skole:www.janderup-skole.skoleintra.dk",
			"Jebjerg Skole:www.jebjergskole.skoleintra.dk",
			"Jelling Friskole:www.jellingfriskole.skoleintra.dk",
			"Jens Jessen-Skolen:www.jensjessenskolen.skoleintra.dk",
			"Jerup Skole:www.jerupskole.skoleintra.dk",
			"Johannesskolen:www.johsskolen.skoleintra.dk",
			"Jægerspris Skole:www.jpskole.skoleintra.dk",
			"Jærgensby-Skolen:www.joeby.skoleintra.dk",
			"Kalundborg 10. Klassecenter:www.k10c.skoleintra.dk",
			"Kalundborg Friskole:www.kalfri.skoleintra.dk",
			"Kangillinnguit Atuarfiat:www.kangillinnguit.skoleintra.dk",
			"Karise Efterskole:www.kaef.skoleintra.dk",
			"Karise Skole:www.gl-karise-skole.skoleintra.dk",
			"Karrebæk Skole:www.karrebaekskole.skoleintra.dk",
			"Karup Skole:www.karupskole.skoleintra.dk",
			"Katrinedalskolen:www.katrinedalskolen.skoleintra.dk",
			"Kattegatskolen:www.kattegatskolen.skoleintra.dk",
			"Kerteminde 10. KlasseCenter:www.kertemindeungdomsskole.skoleintra.dk",
			"KERTEMINDEEGNENS FRISKOLE:www.kertfri.skoleintra.dk",
			"Kibæk skole:www.kibaekskole.skoleintra.dk",
			"Kikhæj Elev- og Aflastningshjem:www.kikhoj.skoleintra.dk",
			"Kildebakken:www.kildebakken.skoleintra.dk",
			"Kildevangens Skole:www.kildevangensskole.skoleintra.dk",
			"Kingoskolen:www.kingoskolen.skoleintra.dk",
			"Kirke Hyllinge Skole:www.krhskole.skoleintra.dk",
			"Kirkebækskolen:www.kirkebaekskolen.skoleintra.dk",
			"Kirke-Helsinge Skole:www.kirke-helsingeskole.skoleintra.dk",
			"Kirkeskolen:www.kirkeskolen.skjern.skoleintra.dk",
			"Kirstine Seligmanns Skole:www.kss.skoleintra.dk",
			"KIRSTINEBJERGSKOLEN:www.kirstinebjergskolen.skoleintra.dk",
			"Kirstinedalsskolen:www.kirstine.skoleintra.dk",
			"Klarup Skole:www.klarupskole.skoleintra.dk",
			"Klippen, Den Kristne Friskole:www.klippen.skoleintra.dk",
			"Klostermarken Skole:www.klostermark.skoleintra.dk",
			"Klostermarksskolen:www.klostermarksskolen.skoleintra.dk",
			"Klovborg Friskole:www.klovborgfriskole.skoleintra.dk",
			"Kolding Realskole:www.koldingrealskole.skoleintra.dk",
			"Kolind Centralskole:www.kolindskole.skoleintra.dk",
			"Kollund Skole og Bærnehus:www.kollundskoleogboernehus.skoleintra.dk",
			"Kongehæjskolen:www.kongehoejskolen.skoleintra.dk",
			"KonTiki - bærnenes skole:www.kontiki.skoleintra.dk",
			"Kornmarkskolen:www.kornmarkskolen.dk",
			"Kornmod Realskole:www.kornmod.skoleintra.dk",
			"Korsholm Skole:www.korsholmskole.skoleintra.dk",
			"Korshæjskolen:www.korshoj.skoleintra.dk",
			"Kostskolen Assersbælgærd:www.assersboelgaard.skoleintra.dk",
			"Kragelund Efterskole:www.kragelund-efterskole.skoleintra.dk",
			"Kratholmskolen:www.kratholmskolen.skoleintra.dk",
			"Kristofferskolen:www.kristofferskolen.skoleintra.dk",
			"Krumsæ Fri- og Kostskole:www.krumsoe.skoleintra.dk",
			"Kvaglundskolen:www.kvaglundskolen.skoleintra.dk",
			"Kvikmarkens Privatskole:www.kvikmarken.skoleintra.dk",
			"Kvong Friskole:www.kvongfriskole.skoleintra.dk",
			"Kærbyskolen:www.kaerbyskolen.skoleintra.dk",
			"Kæge Lille Skole:www.klis.skoleintra.dk",
			"Ladegærdsskolen:www.ladegaardsskolen.skoleintra.dk",
			"Lagoniskolen:www.lagoniskolen.dk",
			"Landsbyskolen:www.landsbyskolen.skoleintra.dk",
			"Landsgrav Friskole:www.landsgravfriskole.skoleintra.dk",
			"Langagerskolen:www.langager.skoleintra.dk",
			"Langebjergskolen:www.langebjergskolen.skoleintra.dk",
			"Langhæjskolen:www.langhoejskolen.skoleintra.dk",
			"Langhæjskolen:www.langhojskolen.skoleintra.dk",
			"Langmarkskolen:www.langmarkskolen.skoleintra.dk",
			"Laursens Realskole:www.laursens-realskole.skoleintra.dk",
			"Learnmark Horsens, Step 10:www.10-klasse.skoleintra.dk",
			"Levring Efterskole:www.levring-efterskole.skoleintra.dk",
			"Lille Egede Friskole:www.lilleegede.skoleintra.dk",
			"Lille Næstved Skole:www.lille-naestved-skole.skoleintra.dk",
			"Lillebæltskolen:www.lillebaeltskolen.skoleintra.dk",
			"Lilleræd Skole:www.lilleroedskole.skoleintra.dk",
			"Lind Skole:www.lind-skole.skoleintra.dk",
			"Lindegærdskolen:www.lindegaardskolen.skoleintra.dk",
			"Lundagerskolen:www.lundagerskolen.skoleintra.dk",
			"Lundebjergskolen:www.lundebjergskolen.skoleintra.dk",
			"Lundergærdskolen:www.lundergaardskolen.skoleintra.dk",
			"Lundgærdskolen:www.lundgaardskolen.skoleintra.dk",
			"Lygten Skole:www.lygtenskole.skoleintra.dk",
			"Lykkegærdskolen:www.lykkegaardskolen.skoleintra.dk",
			"Lykkeskolen:www.lykkeskolen.skoleintra.dk",
			"Lyngbjerggærdskolen:www.lyngbjerggaardskolen.skoleintra.dk",
			"Lyngby Friskole:www.p75.skoleintra.dk",
			"Lyngby Private Skole:www.lps.skoleintra.dk",
			"Lynge Skole:www.lyngeskole.skoleintra.dk",
			"Læk Danske Skole:www.laek-danskeskole.skoleintra.dk",
			"Lærkeskolen:www.laerkeskolen.skoleintra.dk",
			"Læsæ Skole:www.laesoeskole.skoleintra.dk",
			"Lægstær skole:www.logstorskole.skoleintra.dk",
			"Lærslev Friskole:www.loerslevfriskole.skoleintra.dk",
			"Læve-ærslev Skole:www.loeve-oerslev.skoleintra.dk",
			"M.C. Holms Skole:www.mcholms.skoleintra.dk",
			"Maglebjergskolen:www.maglebjergskolen.skoleintra.dk",
			"Maglehæjskolen:www.maglehojskolen.skoleintra.dk",
			"Malling Skole:www.malling-skole.skoleintra.dk",
			"Marbækskolen:www.marbaekskolen.skoleintra.dk",
			"Margrethelyst Friskole:www.margrethelyst.skoleintra.dk",
			"Margretheskolen:www.margrethe-skolen.skoleintra.dk",
			"Maribo Skole Borgerskoleafdelingen:www.by-skolen.skoleintra.dk",
			"Maribo Skole, Centerafdelingen:www.centerafdelingen.skoleintra.dk",
			"Maribo skole, Margretheskoleafdelingen:www.mariboskole-margretheskolen.dk",
			"Marie Kruses Skole:www.mks.skoleintra.dk",
			"Marie Mærks Skole:www.mms.skoleintra.dk",
			"Mariendal Friskole:www.mariendalfriskole.skoleintra.dk",
			"Marievangsskolen:www.marievangsskolen.skoleintra.dk",
			"Markusskolen:www.markusskolen.skoleintra.dk",
			"Mejlby Efterskole:www.mejlbyefterskole.skoleintra.dk",
			"Mejls-Orten-Tinghæj Friskole:www.mot-friskole.skoleintra.dk",
			"Mentiqa Nordjylland:www.mentiqa-nordjylland.skoleintra.dk",
			"Midtskolen:www.midtskolen.skoleintra.dk",
			"Minngortuunnguup Atuarfia:www.minngortuunnguup.skoleintra.dk",
			"Mir skolerne Askovfonden.:www.mir.skoleintra.dk",
			"Morten Bærup Skolen:www.mortenboerup.skoleintra.dk",
			"Munkebakkeskolen:www.munkebakkeskolen.skoleintra.dk",
			"Munkholmskolen:www.munkholmskolen.skoleintra.dk",
			"Mælleholmskolen:www.moelleholmskolen.skoleintra.dk",
			"Mællehæjskolen:www.moellehoejskolen.skoleintra.dk",
			"Mæn Friskole:www.moen-friskole.skoleintra.dk",
			"Mæn Skole:www.moen-skole.skoleintra.dk",
			"Mænsted Skole:www.moensted.skoleintra.dk",
			"Mælæv Skole:www.maaloevskole.skoleintra.dk",
			"N. Zahles Seminarieskole:www.zahle.skoleintra.dk",
			"Nalunnguarfiup Atuarfia:www.nalunng.skoleintra.dk",
			"Naur-Sir Skole:www.naur-sir-sk.skoleintra.dk",
			"Nordagerskolen:www.nordagerskolen.skoleintra.dk",
			"Nordby Skole:www.nordbyskole.skoleintra.dk",
			"Nordre Skole:www.nordresk.skoleintra.dk",
			"Nordskolen:www.nord-skolen.skoleintra.dk",
			"Nordskolen og Nordstjernen:www.nordskolen.skoleintra.dk",
			"Nordvestskolen, Sællested afdeling:www.soellested.skoleintra.dk",
			"Nors Skole:www.nors-skole.skoleintra.dk",
			"Nr Uttrup Skole:www.nruttrup-skole.skoleintra.dk",
			"Nr. Asmindrup Skole og Videnscenter:www.nr.asmindrup.skoleintra.dk",
			"Nr. Lyndelse Friskole:www.nr-lyndelse-friskole.skoleintra.dk",
			"Nr.Vium-Troldhede Skole:www.viumtrold.skoleintra.dk",
			"Nuuk Internationale Friskole:www.nif.skoleintra.dk",
			"Ny Hollænderskolen:www.frb-nh.skoleintra.dk",
			"Nyager Skole:www.nyager.skoleintra.dk",
			"Nyborg Friskole:www.nyborgfriskole.skoleintra.dk",
			"Nyborg Heldagsskole:www.nyborgheldagsskole.skoleintra.dk",
			"Nybæl skole:www.nyboel.skoleintra.dk",
			"Nærum Privatskole:www.naerum-privatskole.skoleintra.dk",
			"Næsbjerg Skole:www.naesbjergskole.skoleintra.dk",
			"Nærre Aaby Realskole:www.naar.skoleintra.dk",
			"Nærre Aaby Skole:www.naas.skoleintra.dk",
			"Nævling Skole:www.noevling-skole.dk",
			"Oddense skole:www.oddenseskole.skoleintra.dk",
			"Odder lille Friskole:www.odderlillefriskole.skoleintra.dk",
			"Odense Friskole:www.odense-friskole.skoleintra.dk",
			"Oksevejens Skole:www.oksevejensskole.skoleintra.dk",
			"Osted fri og efterskole:www.ofe-skole.skoleintra.dk",
			"Osted Skole:www.osted.skoleintra.dk",
			"Overlade Skole:www.overlade-skole.skoleintra.dk",
			"Overlund Skole:www.overlundskole.dk",
			"Paradisbakkeskolen - Svaneke Afdeling:www.svaneke.skoleintra.dk",
			"Paradisbakkeskolen Afdeling Nexæ:www.nexoe.skoleintra.dk",
			"Pilehaveskolen:www.pilehaveskolen.skoleintra.dk",
			"Pilehaveskolen:www.pilehave-skolen.skoleintra.dk",
			"Pindstrupskolen Specialcenter Syddjurs:www.pindstrupskolen.skoleintra.dk",
			"Prins Henriks Skole:www.prinshenriksskole.skoleintra.dk",
			"Privatskolen Als:www.privatskolen-als.skoleintra.dk",
			"Privatskolen i Frederikshavn:www.pif.skoleintra.dk",
			"Privatskolen I Sæby:www.privatskolenisaeby.skoleintra.dk",
			"Projekt Gjessæs Bærn:www.gjessoe-skole.skoleintra.dk",
			"Præstegærdsskolen:www.praestegaardsskolen.skoleintra.dk",
			"Præstemarkskolen:www.praestemarkskolen.skoleintra.dk",
			"Præstemoseskolen:www.praestemoseskolen.skoleintra.dk",
			"Præstæ Privatskole:www.praestoeprivatskole.skoleintra.dk",
			"Præstæ Skole:www.praestoe-skole.skoleintra.dk",
			"Pædagogisk Center Haderslev:www.pd-center.skoleintra.dk",
			"Randers Kristne Friskole:www.randers-kristne-friskole.skoleintra.dk",
			"Randers Realskole:www.randers-realsk.skoleintra.dk",
			"Rask Mælle Skole:www.rask-moelle-skole.dk",
			"Ravnkilde Skole:www.ravnkildeskole.skoleintra.dk",
			"Ravnsholtskolen:www.ravnsholtskolen.skoleintra.dk",
			"Ravsted Bærneunivers:www.ravstedskole.skoleintra.dk",
			"Reerslev skole:www.reerslev.skoleintra.dk",
			"Rens Ungdomsskole:www.rensungdomsskole.skoleintra.dk",
			"Resen Skole:www.resen-skole.skoleintra.dk",
			"Resenbro Skole:www.resenbro.skoleintra.dk",
			"Ringe Friskole:www.ringe-friskole.skoleintra.dk",
			"Ringkæbing-Skjern Kommune:www.rksk.skoleintra.dk",
			"Ringsted Ny Friskole:www.ringstednyfriskole.skoleintra.dk",
			"Rinkenæs Skole:www.rinkenaesskole.skoleintra.dk",
			"Risbjergskolen:www.risbjergskolen.skoleintra.dk",
			"Rolighedsskolen:www.rolighedsskolen.skoleintra.dk",
			"Ros Privatskole:www.rosps.skoleintra.dk",
			"Rosendalskolen:www.rosendalskolen.skoleintra.dk",
			"Rosenvangskolen:www.rosenvangskolen.skoleintra.dk",
			"Rosenvængets Skole:www.rosenvaengetsskole.skoleintra.dk",
			"Roskilde Kommune, Skoleafdelingen:www.roskildeskoler.skoleintra.dk",
			"Roskilde Lille Skole:www.rlsintra.dk",
			"Roskilde Private Realskole:www.rprroskilde.skoleintra.dk",
			"Roslev Skole:www.roslev-skole.skoleintra.dk",
			"Rosmus Skole:www.rosmusskole.skoleintra.dk",
			"Rudolf Steiner Skolen Kvistgærd:www.steinerskolen-kvistgaard.skoleintra.dk",
			"Rudolf Steiner-Skolen i Odense:www.rss-odense.skoleintra.dk",
			"Rudolf Steiner-Skolen i ærhus:www.sydskolen.skoleintra.dk",
			"Rygaards Skole:www.rygaards.skoleintra.dk",
			"Rynkevangskolen:www.rynkevangskolen.skoleintra.dk",
			"Rækker Mælle Skolen:www.raekkermoelleskolen.skoleintra.dk",
			"Rævebakkeskolen:www.raevebakkeskolen.skoleintra.dk",
			"Rædkærsbro Skole:www.roedkaersbro.skoleintra.dk",
			"Rænde Hæjskole og Efterskole:www.rhe.skoleintra.dk",
			"Rænde Privatskole:www.roende-privatskole.skoleintra.dk",
			"Rænnebæk skole:www.roennebaekskole.skoleintra.dk",
			"Rænneskolen afdeling æstre:www.oestre-skole.skoleintra.dk",
			"Rænneskolen, afd. Sændermark:www.soendermark.skoleintra.dk",
			"Rærby Skole:www.roerby-skole.skoleintra.dk",
			"Rærkjær Skole:www.roerkjaerskole.skoleintra.dk",
			"Sabro-Korsvejskolen:www.sabro-korsvejskolen.skoleintra.dk",
			"Sakskæbing Skole:www.sakskoebing.skoleintra.dk",
			"Salbrovadskolen:www.salbrovadskolen.skoleintra.dk",
			"Saltum Skole:www.saltum-centralskole.skoleintra.dk",
			"Samsæ Skole:www.tranebjerg-skole.skoleintra.dk",
			"Sankt Annæ Skole:www.sanktannae.skoleintra.dk",
			"Sankt Ansgars Skole:www.ansgarsskole.skoleintra.dk",
			"Sankt Birgitta Skole:www.sanktbirgitta.dk",
			"Sankt Pauls Skole:www.sanktpaulsskole.skoleintra.dk",
			"Sankt Petri Skole/ Sankt-Petri Schule:www.sanktpetriskole.skoleintra.dk",
			"Sct. Albani Skole:www.sctalbaniskole.skoleintra.dk",
			"Sct. Ibs Skole:www.sctibs.skoleintra.dk",
			"Sct. Joseph Skole:www.sctjoseph.skoleintra.dk",
			"Sct. Joseph Sæstrenes Skole:www.sct-joseph.skoleintra.dk",
			"Sct. Jærgens Skole:www.sjs.skoleintra.dk",
			"Sct. Knuds Skole:www.sct-knud.skoleintra.dk",
			"Sdr. Felding Skole:www.sdrfelding-skole.skoleintra.dk",
			"Sebber Skole Landsbyordning:www.sebberskole.skoleintra.dk",
			"Sejergaardsskolen:www.sejergaardsskolen.skoleintra.dk",
			"Sejeræ Skole:www.sejeroeskole.skoleintra.dk",
			"Sengelæse skole:www.sengeloese.skoleintra.dk",
			"Sennels Skole:www.sennels-skole.skoleintra.dk",
			"Sigerslevæster Privatskole:www.sigerslev.skoleintra.dk",
			"SIM - Skolen i Midten:www.skolenimidten.skoleintra.dk",
			"Sindal Privatskole:www.sindalprivatskole.skoleintra.dk",
			"Sindal Skole:sindal.skoleintra.dk",
			"Sjælsæskolen:www.sjaelsoe.skoleintra.dk",
			"Sjærring Skole:www.sjoerring-skole.skoleintra.dk",
			"Skads Skole:www.skadsskole.skoleintra.dk",
			"Skagen Skole:www.skagenskole.skoleintra.dk",
			"Skanderborgskolen:www.skanderborgskolen.skoleintra.dk",
			"Skarrild Skole:www.skarild-skarild.skoleintra.dk",
			"Skelbæk Friskole:www.skelbaekfriskole.skoleintra.dk",
			"Skelgærdsskolen:www.sg.skoleintra.dk",
			"Skipper Clement Skolen:www.skipper-clement-skolen.skoleintra.dk",
			"Skivehus Skole:www.skivehusskole.skoleintra.dk",
			"Skjern Kristne Friskole:www.skrif.skoleintra.dk",
			"Skole Nord - afdeling Kongeskær:www.kongeskaer.skoleintra.dk",
			"Skole Nord - afdeling Svartingedal:www.svartingedal.skoleintra.dk",
			"Skole, SFO og Bærnehave:www.fjelstedharndrupskole.skoleintra.dk",
			"Skolecenter Jetsmark:www.skolecenterjetsmark.dk",
			"Skolegades Skole - Serritslev Skole:www.skolegadesskole.skoleintra.dk",
			"Skolen pæ Duevej:www.duevej.skoleintra.dk",
			"Skolen pæ Nyelandsvej:www.frb-ny.skoleintra.dk",
			"Skolen ved Bulowsvej:www.skolenbulowsvej.skoleintra.dk",
			"Skolen ved Nordens Plads:www.skolenvednordensplads.skoleintra.dk",
			"Skolen ved Rænnebær Allæ:www.gl-ronnebaeralle.skoleintra.dk",
			"Skolen ved Stadion:www.svs.skoleintra.dk",
			"Skolen ved Tuse Næs:www.skolenvedtusenaes.skoleintra.dk",
			"Skolerne i Snekkersten:www.snekkerstenskole.skoleintra.dk",
			"Skorpeskolen Privatskole:www.skorpeskolen.skoleintra.dk",
			"Skovboskolen:www.skovboskolen.dk",
			"Skovlyskolen:www.skovlyskolen.skoleintra.dk",
			"Skovsgærd Tranum Skole:www.skovsgaardtranumskole.dk",
			"Skovvangskolen:www.skovvang.skoleintra.dk",
			"Skovvangskolen:www.skov-vang-skolen.skoleintra.dk",
			"Skrillingeskolen:www.skrillingeskolen.skoleintra.dk",
			"Skt Knuds Skole:www.sktknudsskole.skoleintra.dk",
			"Skæring Skole:www.skaering-skole.skoleintra.dk",
			"Skædstrup Skole:www.skoedstrup-skole.skoleintra.dk",
			"Slagslunde Skole:www.slagslunde-skole.skoleintra.dk",
			"Slotsparkens Friskole:www.spfs.skoleintra.dk",
			"Smidstrup-Skærup Skole:www.smidstrup-skole.skoleintra.dk",
			"Snedsted Skole:www.snedsted-skole.skoleintra.dk",
			"Sofiehæj Friskole:www.sofiehoej-friskole.skoleintra.dk",
			"Sofielundskolen:www.sofielundskolen.skoleintra.dk",
			"Solhverv Privatskole:www.solhverv.skoleintra.dk",
			"Solsideskolen:www.solsideskolen.skoleintra.dk",
			"Sorring skole:www.sorringskole.skoleintra.dk",
			"Sortebakkeskolen:www.sortebakkeskolen.skoleintra.dk",
			"Soræ Privatskole:www.sprs.skoleintra.dk",
			"Spangsbjergskolen:www.spangsbjergskolen.skoleintra.dk",
			"Spejderskolen - Korinth Efterskole:www.spejderskolen-korinth.skoleintra.dk",
			"Spjald Skole:www.spjald-skole.skoleintra.dk",
			"Spjellerup Friskole:www.spjellerup.skoleintra.dk",
			"SPOR 10:www.spor10.skoleintra.dk",
			"Sportsefterskolen SINE:www.sine.skoleintra.dk",
			"SPS - Sjællands Privatskole:www.spsnet.skoleintra.dk",
			"St. Magleby Skole:www.storemaglebyskole.dk",
			"Stauning Skole:www.stauningskole.skoleintra.dk",
			"Stengærdsskolen:www.stengaardsskolen.skoleintra.dk",
			"Stenoskolen:www.stenoskolen.skoleintra.dk",
			"Stepping Friskole:www.steppingfriskole.skoleintra.dk",
			"Stevns Dagskole:www.stevnsdagskole.skoleintra.dk",
			"Stevns Friskole:www.stevnsfriskole.skoleintra.dk",
			"Stige Friskole:www.stigefriskole.skoleintra.dk",
			"Stillinge Skole:www.stillingeskole.skoleintra.dk",
			"Stokkebækskolen:www.stokkebaekskolen.dk",
			"Store Andst Efterskole:www.storeandstefterskole.skoleintra.dk",
			"Strandby Skole:www.strandbyskole.skoleintra.dk",
			"Strandgærdskolen:www.strandgaardskolen.skoleintra.dk",
			"Strib Skole:www.stribskole.skoleintra.dk",
			"Struer æstre Skole:www.struer-oestre.skoleintra.dk",
			"STU - Lolland:www.stu-lolland.skoleintra.dk",
			"STU Viborg:www.stu-viborg.skoleintra.dk",
			"Stubbæk Skole:www.stubbaekskole.skoleintra.dk",
			"Studie 10:www.studie10.skoleintra.dk",
			"Suldrup Skole:www.suldrup-skole.skoleintra.dk",
			"Svallerup Skole:www.svallerup.skoleintra.dk",
			"Svenstrup Skole:www.svenstrupskole.skoleintra.dk",
			"Sværdborg Friskole/Privatskole:www.svaerdborgfriskole.skoleintra.dk",
			"Sydbornholms Privatskole:www.sydbornholms.skoleintra.dk",
			"Sydmors Skole og Bærnehus:www.ny-mors.skoleintra.dk",
			"Sydskolen:www.syd-skolen.skoleintra.dk",
			"Sæby Hallenslev Friskole:www.shfri.skoleintra.dk",
			"Sædding Efterskole:www.saedding.skoleintra.dk",
			"SæRSLEV-HæRSLEV-SKOLEN:www.saerslev-haarslev.skoleintra.dk",
			"Sæagerskolen:www.soeagerskolen.skoleintra.dk",
			"Sæborg Privatskole & Skovbærnehave:www.spskole.skoleintra.dk",
			"Sæborg Skole:www.soeborgskole.skoleintra.dk",
			"Sædalskolen:www.sodalskolen.dk",
			"Sægærd Friskole:www.soegaard-friskole.skoleintra.dk",
			"Sægærden:www.soegaarden.skoleintra.dk",
			"Sælystskolen Silkeborg:www.soelystskolen.silkeborg.dk",
			"Sænderborg International School:www.sischool.skoleintra.dk",
			"Sænderbroskolen:www.soenderbroskolen.skoleintra.dk",
			"Sændergades Skole:www.sondergades-skole.skoleintra.dk",
			"Sænderholm Skole:www.soenderholmskole.skoleintra.dk",
			"Sændermarkskolen:www.sondermark-skolen.skoleintra.dk",
			"Sændermarksskolen:www.sondermarksskolen.skoleintra.dk",
			"Sænderskov-Skolen:www.soenderskov-skolen.skoleintra.dk",
			"Sændersæskolen:www.sonderso-sk.skoleintra.dk",
			"Sændre Skole:www.soendreskole.skoleintra.dk",
			"Sændre Skole Rædby:www.rodbyskole.skoleintra.dk",
			"Tallerupskolen:www.tallerupskolen.skoleintra.dk",
			"Tarm Skole:www.tarm-skole.skoleintra.dk",
			"Th. Langs Skole:www.thlang.skoleintra.dk",
			"THOMASSKOLEN:www.thomasskolen.skoleintra.dk",
			"Thorstrup Skole:www.thorstrupskole.skoleintra.dk",
			"TIEREN:www.tieren-skive.skoleintra.dk",
			"Tilsted Skole:www.tilsted-skole.skoleintra.dk",
			"Tim Skole:www.tim-skole.skoleintra.dk",
			"Tingstrup Skole:www.tingstrup-skole.skoleintra.dk",
			"Tjæreborg Skole:www.tjaereborgskolen.skoleintra.dk",
			"Tjærring Skole:www.tjoerring-skole.skoleintra.dk",
			"Toftehæjskolen:www.toftehojskolen.skoleintra.dk",
			"Toppedalskolen:www.toppedalskolen.skoleintra.dk",
			"Tornvedskolen:www.tornvedskolen.skoleintra.dk",
			"Torstorp skole:www.torstorpskole.skoleintra.dk",
			"Tællæse Privat- og Efterskole:www.tpoe.skoleintra.dk",
			"Tænning-Træden Friskole:www.ttf-friskole.skoleintra.dk",
			"Tærnborg Skole:www.taarnborgskole.skoleintra.dk",
			"UCS10:www.ucs10.skoleintra.dk",
			"Uddannelsescenter Mariebjerg:www.uddannelsescentermariebjerg.skoleintra.dk",
			"Udefriskolen:www.udefriskolen.skoleintra.dk",
			"Udviklingscenter Skiftesporet:www.skiftesporet.skoleintra.dk",
			"Ugelbælle Friskole:www.ugelboellefriskole.skoleintra.dk",
			"Uhre Friskole:www.uhrefriskole.skoleintra.dk",
			"Ulbjerg Skole:www.ulbjergskole.skoleintra.dk",
			"Ulkebæl Skole:www.ulkebol.skoleintra.dk",
			"Ullerup Bæk Skolen:www.ullerupbaek.skoleintra.dk",
			"Ung i Aarhus:www.uiaa.skoleintra.dk",
			"Ungdomscenteret i Glostrup - 10eren:www.uc-glostrup.skoleintra.dk",
			"Ungdomsskolen:www.tusk.skoleintra.dk",
			"Ungdomsskolen Amager:www.us.amager.skoleintra.dk",
			"UngHolbæk:www.lystavlen.skoleintra.dk",
			"UngNorddjurs:www.ungnorddjurs.skoleintra.dk",
			"Ungaalborg Uddannelsescenter:www.ungauc.skoleintra.dk",
			"Vadehavsskolen:www.vadehavsskolen.skoleintra.dk",
			"Vadgærd Skole:www.vadgaardskole.skoleintra.dk",
			"Valhæj Skole:www.valhoj-skole.skoleintra.dk",
			"Vallekilde-Hærve Skole:www.gl-vallekilde-horveskole.skoleintra.dk",
			"Vallensbæk Skole:www.vallensbaekskole.skoleintra.dk",
			"Vamdrup Skole:www.vamdrupskole.skoleintra.dk",
			"Varnæs skole:www.varnaes-skole.skoleintra.dk",
			"Vejen Friskole:www.vejenfriskole.skoleintra.dk",
			"Vejgærd æstre Skole:www.vejgaardoestreskole.skoleintra.dk",
			"Vejlebroskolen:www.vejlebroskolen.skoleintra.dk",
			"Vejrup Skole:www.vejrupskole.skoleintra.dk",
			"Veksæ Skole:www.veksoe-skole.skoleintra.dk",
			"Vemmedrupskolen:www.vemmedrupskolen.dk",
			"Vemmelev Skole:www.skole.korsoer.skoleintra.dk",
			"Venæ Efterskole:www.venoe-efterskole.skoleintra.dk",
			"Veslæs - æslæs skole:www.vesloes-skole.skoleintra.dk",
			"Vestegnens Privatskole:www.vestegnens-privatskole.skoleintra.dk",
			"Vester Hassing Skole:www.vesterhassingskole.skoleintra.dk",
			"Vester Hornum Skole:www.vester-hornum-skole.skoleintra.dk",
			"Vesterbakkeskolen:www.vesterbakkeskolen.skoleintra.dk",
			"Vestergærdsskolen:www.vestergaardsskolen.dk",
			"Vestermarkskolen:www.vestermarkskolen.skoleintra.dk",
			"Vestervangskolen:www.vestervang-skolen-herning.skoleintra.dk",
			"Vestre Skole:www.vestreskole-middelfart.skoleintra.dk",
			"Vestrup Skole:www.vestrupskole.skoleintra.dk",
			"Vestsalling Skole og Dagtilbud:www.vestsallingskoleogdagtilbud.skoleintra.dk",
			"Vibeholmskolen:www.vibeholmskolen.skoleintra.dk",
			"Vibeskolen:www.vibeskolen.dk",
			"Viborg Private Realskole:www.viborgrealskole.skoleintra.dk",
			"Vildbjerg Skole:www.vildbjerg-skole.skoleintra.dk",
			"Vindblæs Friskole:www.vindblaes.skoleintra.dk",
			"Vinde Helsinge Friskole-Vestsjæl. Idrætsefterskole:www.vhfe.skoleintra.dk",
			"Vinderslev Skole:www.vinderslevskole.skoleintra.dk",
			"Vinderup Realskole:www.vinderup-realskole.skoleintra.dk",
			"Vinding Skole:www.vinding-skole.skoleintra.dk",
			"Virupskolen:www.virupskolen.skoleintra.dk",
			"Vissenbjerg Skole:www.vissenbjergskole.skoleintra.dk",
			"Vitaskolen:www.vitaskolen.skoleintra.dk",
			"Vittenbergskolen:www.vittenbergskolen.skoleintra.dk",
			"Vivild Bærneby:www.langhoj.skoleintra.dk",
			"Voel Skole:www.voel-skole.skoleintra.dk",
			"Vor Frue Skole:www.vorfrue-skole.skoleintra.dk",
			"Vorbasse Skole Bærnehuset Andedammen:www.vorbasseskole.skoleintra.dk",
			"Vorgod-Barde Skole:www.vorgodbardeskole.dk",
			"Vræ Skole:www.vraa-skole.skoleintra.dk",
			"VSU Haderslev:www.vsu-haderslev.skoleintra.dk",
			"Waldemarsbo Efterskole:www.waldemarsbo.skoleintra.dk",
			"Xclass 10. Klasse:www.xclass.skoleintra.dk",
			"æræ Efterskole:www.aeroe-efterskole.skoleintra.dk",
			"æ. Brænderslev Centralskole:www.oebc.skoleintra.dk",
			"ælstykke 10. klassecenter:www.oelstykketiende.skoleintra.dk",
			"ærebroskolen:www.oerebroskolen.skoleintra.dk",
			"ærkildskolen:www.orkildskolen.skoleintra.dk",
			"ærslevkloster Skole:www.oerslevklosterskole.skoleintra.dk",
			"ærsted Bærneby:www.rougsoeskolen.skoleintra.dk",
			"ærum skole:www.orumskole.dk",
			"æster æby Friskole:www.voresfriskole.skoleintra.dk",
			"æsterbro Lilleskole:www.oesterbrolilleskole.skoleintra.dk",
			"æsterbyskolen:www.osterbyskolen.skoleintra.dk",
			"æsterhæjskolen:www.osterhojskolen.skoleintra.dk",
			"æsterild Skole:www.oesterild-skole.skoleintra.dk",
			"æstervangskolen:www.oestervangskolen.skoleintra.dk",
			"æstre Skole:www.gl-holbost.skoleintra.dk",
			"æstre Skole:www.ostre-skole.skoleintra.dk",
			"Aabenraa Friskole:www.aabenraafriskole.skoleintra.dk",
			"Aabybro Skole:www.aabybroskole.skoleintra.dk",
			"Aabæk Efterskole:www.aabaek.skoleintra.dk",
			"ædalens Privatskole:www.aadalprivat.skoleintra.dk",
			"ædalens Skole:www.aadalensskole.skoleintra.dk",
			"ædalskolen:www.aadal-skolen.skoleintra.dk",
			"ædalskolen:www.aadalskolen-skive.skoleintra.dk",
			"ædalskolen, Fangel Friskole:www.aada.skoleintra.dk",
			"ædum Bærneunivers:www.aadum-skole.skoleintra.dk",
			"ægærd Efterskole:www.aagaardefterskole.skoleintra.dk",
			"Aakjærskolen:www.aakjaerskolen.skoleintra.dk",
			"Aalbæk Skole:www.aalbaekskole.skoleintra.dk",
			"ærhus Privatskole:www.aarhusprivatskole.skoleintra.dk",
			"Aars Skole:www.aars-skole.skoleintra.dk",
			"Aarupskolen:www.aarupskolen.skoleintra.dk",
			"Aastrup Skole:www.aastrupskole.skoleintra.dk" };
	*/

	private static Set<String> getKeyList(Context ctx, String key) {
		SharedPreferences p = getPrefs(ctx);
		Map<String, ?> all = p.getAll();

		Set<String> result = new TreeSet<String>();

		for (String k : all.keySet()) {
			if (k.startsWith(key)) {
				String s = k.replace(key, "");
				if (s.length() > 0) {
					result.add(s);
				}
			}
		}

		return result;
	}

	public static Set<String> getClassList(Context ctx) {
		return getKeyList(ctx, CLASSDESCRIPTION);
	}

	public static Set<String> getTeacherList(Context ctx) {
		return getKeyList(ctx, TEACHERDESCRIPTION);
	}

	public static Set<String> getStudentList(Context ctx) {
		return getKeyList(ctx, STUDENTPFX);
	}

	public static void setStudentName(Context ctx, String key, String value) {
		setPrefString(ctx, STUDENTPFX + key, value);
	}

	public static String getStudentName(Context ctx, String key) {
		return getPrefString(ctx, STUDENTPFX + key);
	}

}
