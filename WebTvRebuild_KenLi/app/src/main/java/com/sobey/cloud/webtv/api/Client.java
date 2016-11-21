package com.sobey.cloud.webtv.api;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * ================================================
 * Discuz! Ucenter API for JAVA
 * ================================================
 * UC Client 閫氱敤璋冪敤璇ョ被鏉ュ疄鐜颁笌UC Server涔嬮棿鐨勯�淇°�
 * 璇ョ被瀹炵幇涓嶶C Server閫氫俊鐨勬墍鏈夋帴鍙ｅ嚱鏁�
 * 
 * 鏇村淇℃伅锛歨ttp://code.google.com/p/discuz-ucenter-api-for-java/
 * 浣滆�锛氭骞�(no_ten@163.com) 
 * 鍒涘缓鏃堕棿锛�009-2-20
 */
public class Client extends PHPFunctions{

	public static boolean IN_UC = true;
	public static String UC_IP = "127.0.0.1";
	public static String UC_API = "http://localhost/uc";
	public static String UC_CONNECT = "";
	public static String UC_KEY = "VeK62632Zf7cQcs1G2o6Jew3A9M1MdF055tbOc305b44Q9y7B8bah0F59aO6h27d";
	public static String UC_APPID = "3";
	public static String UC_CLIENT_VERSION = "1.0";
	public static String UC_CLIENT_RELEASE = "20090212";
	public static String UC_ROOT = "";		//note 鐢ㄦ埛涓績瀹㈡埛绔殑鏍圭洰褰�UC_CLIENTROOT
	public static String UC_DATADIR = UC_ROOT+"./data/";		//note 鐢ㄦ埛涓績鐨勬暟鎹紦瀛樼洰褰�
	public static String UC_DATAURL = "UC_API"+"/data";			//note 鐢ㄦ埛涓績鐨勬暟鎹�URL
	public static String UC_API_FUNC = UC_CONNECT.equals("mysql") ? "uc_api_mysql" : "uc_api_post";
	public static String[] uc_controls = {};
	
	static {
	    InputStream in = Client.class.getClassLoader().getResourceAsStream("config.properties");
	    Properties properties = new Properties();
	    try {
			properties.load(in);
			UC_API = properties.getProperty("UC_API");
			UC_IP = properties.getProperty("UC_IP");
//			UC_KEY = properties.getProperty("UC_KEY");
			UC_APPID = properties.getProperty("UC_APPID");
			UC_CONNECT = properties.getProperty("UC_CONNECT");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected String uc_serialize(String $arr, int $htmlon) {
		//return xml_serialize($arr, $htmlon);
		return $arr;
	}

	protected String uc_unserialize(String $s) {
		//include_once UC_ROOT.'./lib/xml.class.php';
		//return xml_unserialize($s);
		return $s;
	}
	
	protected String uc_addslashes(String $string, int $force , boolean $strip ) {
//		!defined('MAGIC_QUOTES_GPC') && define('MAGIC_QUOTES_GPC', get_magic_quotes_gpc());
//		if(!MAGIC_QUOTES_GPC || $force) {
//			if(is_array($string)) {
//				foreach($string as $key => $val) {
//					$string[$key] = uc_addslashes($val, $force, $strip);
//				}
//			} else {
//				$string = addslashes($strip ? stripslashes($string) : $string);
//			}
//		}
		return $string;
	}
	
	protected String daddslashes(String $string, int $force) {
		return uc_addslashes($string, $force, false);
	}
	
	protected String uc_stripslashes(String $string) {
//		!defined('MAGIC_QUOTES_GPC') && define('MAGIC_QUOTES_GPC', get_magic_quotes_gpc());
//		if(MAGIC_QUOTES_GPC) {
//			return stripslashes($string);
//		} else {
			return $string;
//		}
	}
//----------------------------------------------post鏂瑰紡--------------------------------------------------------
	public String uc_api_post(String $module, String $action, Map<String,Object> $arg ) {
		StringBuffer $s = new StringBuffer();
		String $sep = "";
		//foreach($arg as $k => $v) {
		for (String $k : $arg.keySet()) {
			//$k = ($k);
			Object $v = $arg.get($k);
			$k = urlencode($k);
			
			if($v.getClass().isAssignableFrom(Map.class)) {
				String $s2 = "";
				String $sep2 = "";
				//foreach($v as $k2 => $v2) {
				for(String $k2 : ((Map<String,Object>)$v).keySet() ){
					Object $v2 = ((Map<String,Object>)$v).get($k2);
					$k2 = urlencode($k2);
					$s2 += $sep2+"{"+$k+"}["+$k2+"]="+urlencode(uc_stripslashes( String.valueOf( $v2 )));
					$sep2 = "&";
				}
				$s.append($sep).append($s2);
			} else {
				$s.append($sep).append($k).append("=").append(urlencode(uc_stripslashes( String.valueOf($v) )));
			}
			$sep = "&";
		}
		String $postdata = uc_api_requestdata($module, $action, $s.toString(),"");
		return uc_fopen2(UC_API+"/index.php", 500000, $postdata, "", true, UC_IP, 20, true);
	}
	
	
	/**
	 * 鏋勯�鍙戦�缁欑敤鎴蜂腑蹇冪殑璇锋眰鏁版嵁
	 *
	 * @param string $module	璇锋眰鐨勬ā鍧�
	 * @param string $action	璇锋眰鐨勫姩浣�
	 * @param string $arg		鍙傛暟锛堜細鍔犲瘑鐨勬柟寮忎紶閫侊級
	 * @param string $extra		闄勫姞鍙傛暟锛堜紶閫佹椂涓嶅姞瀵嗭級
	 * @return string
	 */
	protected String uc_api_requestdata(String $module, String $action, String $arg, String $extra) {
		String $input = uc_api_input($arg);
		String $post = "m="+$module+"&a="+$action+"&inajax=2&release="+UC_CLIENT_RELEASE+"&input="+$input+"&appid="+UC_APPID+$extra;
		return $post;
	}

	protected String uc_api_url(String $module, String $action, String $arg, String $extra) {
		String $url = UC_API+"/index.php?"+uc_api_requestdata($module, $action, $arg, $extra);
		return $url;
	}

	public String uc_api_input(String $data) {
		//String $s = $data;
		//String $s = urlencode(uc_authcode($data+"&agent="+md5($_SERVER["HTTP_USER_AGENT"])+"&time="+time(), "ENCODE", UC_KEY));
		//String $s = urlencode(uc_authcode($data+"&agent="+md5("Java/1.5.0_01")+"&time="+time(), "ENCODE", UC_KEY));
		String $s = urlencode(uc_authcode($data+"&agent="+md5("")+"&time="+time(), "ENCODE", UC_KEY));
		return $s;
	}
	
	/**
	 * MYSQL 鏂瑰紡鍙栨寚瀹氱殑妯″潡鍜屽姩浣滅殑鏁版嵁
	 *
	 * @param string $model		璇锋眰鐨勬ā鍧�
	 * @param string $action	璇锋眰鐨勫姩浣�
	 * @param string $args		鍙傛暟锛堜細鍔犲瘑鐨勬柟寮忎紶閫侊級
	 * @return mix
	 */
	public String uc_api_mysql(String $model, String $action, Map $args) {
//		global $uc_controls;
//		if(empty($uc_controls[$model])) {
//			include_once UC_ROOT.'./lib/db.class.php';
//			include_once UC_ROOT.'./model/base.php';
//			include_once UC_ROOT."./control/$model.php";
//			eval("\$uc_controls['$model'] = new {$model}control();");
//		}
		if($action.charAt(0) != '_') {
//			$args = uc_addslashes($args, 1, true);
//			$action = "on"+$action;
//			$uc_controls[$model]->input = $args;
//			return $uc_controls[$model]->$action($args);
			return null;
		} else {
			return "";
		}
	}
	/**
	 * 瀛楃涓插姞瀵嗕互鍙婅В瀵嗗嚱鏁�
	 *
	 * @param string $string	鍘熸枃鎴栬�瀵嗘枃
	 * @param string $operation	鎿嶄綔(ENCODE | DECODE), 榛樿涓�DECODE
	 * @param string $key		瀵嗛挜
	 * @param int $expiry		瀵嗘枃鏈夋晥鏈� 鍔犲瘑鏃跺�鏈夋晥锛�鍗曚綅 绉掞紝0 涓烘案涔呮湁鏁�
	 * @return string		澶勭悊鍚庣殑 鍘熸枃鎴栬� 缁忚繃 base64_encode 澶勭悊鍚庣殑瀵嗘枃
	 *
	 * @example
	 *
	 * 	$a = authcode('abc', 'ENCODE', 'key');
	 * 	$b = authcode($a, 'DECODE', 'key');  // $b(abc)
	 *
	 * 	$a = authcode('abc', 'ENCODE', 'key', 3600);
	 * 	$b = authcode('abc', 'DECODE', 'key'); // 鍦ㄤ竴涓皬鏃跺唴锛�b(abc)锛屽惁鍒�$b 涓虹┖
	 */
	public String uc_authcode(String $string, String $operation){
		return uc_authcode($string, $operation, null);
	}
	public String uc_authcode(String $string, String $operation, String $key){
		return uc_authcode($string, $operation, UC_KEY, 0);
	}
	public String uc_authcode(String $string, String $operation, String $key,int $expiry ) {

		int $ckey_length = 4;	//note 闅忔満瀵嗛挜闀垮害 鍙栧� 0-32;
					//note 鍔犲叆闅忔満瀵嗛挜锛屽彲浠ヤ护瀵嗘枃鏃犱换浣曡寰嬶紝鍗充究鏄師鏂囧拰瀵嗛挜瀹屽叏鐩稿悓锛屽姞瀵嗙粨鏋滀篃浼氭瘡娆′笉鍚岋紝澧炲ぇ鐮磋В闅惧害銆�
					//note 鍙栧�瓒婂ぇ锛屽瘑鏂囧彉鍔ㄨ寰嬭秺澶э紝瀵嗘枃鍙樺寲 = 16 鐨�$ckey_length 娆℃柟
					//note 褰撴鍊间负 0 鏃讹紝鍒欎笉浜х敓闅忔満瀵嗛挜

		$key = md5( $key!=null ? $key : UC_KEY);
		String $keya = md5(substr($key, 0, 16));
		String $keyb = md5(substr($key, 16, 16));
		String $keyc = $ckey_length > 0? ($operation.equals("DECODE") ? substr($string, 0, $ckey_length): substr(md5(microtime()), -$ckey_length)) : "";

		String $cryptkey = $keya + md5( $keya + $keyc);
		int $key_length = $cryptkey.length();

		$string = $operation.equals("DECODE") ? base64_decode(substr($string, $ckey_length)) : sprintf("%010d", $expiry>0 ? $expiry + time() : 0)+substr(md5($string+$keyb), 0, 16)+$string;
		int $string_length = $string.length();

		StringBuffer $result1 = new StringBuffer();

		int[] $box = new int[256];
		for(int i=0;i<256;i++){
			$box[i] = i;
		}

		int[] $rndkey = new int[256];
		for(int $i = 0; $i <= 255; $i++) {
			$rndkey[$i] = (int)$cryptkey.charAt($i % $key_length);
		}

		int $j=0;
		for(int $i = 0; $i < 256; $i++) {
			$j = ($j + $box[$i] + $rndkey[$i]) % 256;
			int $tmp = $box[$i];
			$box[$i] = $box[$j];
			$box[$j] = $tmp;
		}

		$j=0;
		int $a=0;
		for(int $i = 0; $i < $string_length; $i++) {
			$a = ($a + 1) % 256;
			$j = ($j + $box[$a]) % 256;
			int $tmp = $box[$a];
			$box[$a] = $box[$j];
			$box[$j] = $tmp;
			
			$result1.append((char)( ((int)$string.charAt($i)) ^ ($box[($box[$a] + $box[$j]) % 256])));
			
		}

		if($operation.equals("DECODE")) {
			String $result = $result1.substring(0, $result1.length());
			if((Integer.parseInt(substr($result.toString(), 0, 10)) == 0 || Long.parseLong(substr($result.toString(), 0, 10)) - time() > 0) && substr($result.toString(), 10, 16).equals( substr(md5(substr($result.toString(), 26)+ $keyb), 0, 16))) {
				return substr($result.toString(), 26);
			} else {
				return "";
			}
		} else {
			return $keyc+base64_encode($result1.toString()).replaceAll("=", "");
		}
	}


	/**
	 *  杩滅▼鎵撳紑URL
	 *  @param string $url		鎵撳紑鐨剈rl锛屻�濡�http://www.baidu.com/123.htm
	 *  @param int $limit		鍙栬繑鍥炵殑鏁版嵁鐨勯暱搴�
	 *  @param string $post		瑕佸彂閫佺殑 POST 鏁版嵁锛屽uid=1&password=1234
	 *  @param string $cookie	瑕佹ā鎷熺殑 COOKIE 鏁版嵁锛屽uid=123&auth=a2323sd2323
	 *  @param bool $bysocket	TRUE/FALSE 鏄惁閫氳繃SOCKET鎵撳紑
	 *  @param string $ip		IP鍦板潃
	 *  @param int $timeout		杩炴帴瓒呮椂鏃堕棿
	 *  @param bool $block		鏄惁涓洪樆濉炴ā寮�defaul valuet:true
	 *  @return			鍙栧埌鐨勫瓧绗︿覆
	 */
	protected String uc_fopen2(String $url, int $limit , String $post, String $cookie, boolean $bysocket, String $ip, int $timeout, boolean $block) {
//		long $__times__ = isset($_GET["__times__"]) ? intval($_GET["__times__"]) + 1 : 1;
//		if($__times__ > 2) {
//			return "";
//		}
		$url += $url.indexOf("?")>0? "&" : "?"  +"__times__=1";
		return uc_fopen($url, $limit, $post, $cookie, $bysocket, $ip, $timeout, $block);
	}

	protected String uc_fopen(String $url, int $limit, String $post, String $cookie, boolean $bysocket ,String $ip , int $timeout , boolean $block ) {
		String $return = "";

		URL $matches;
		String $host="";
		String $path="";
		int $port = 80;
		try {
			$matches = new URL($url);
			$host = $matches.getHost();
			$path = $matches.getPath()!=null? $matches.getPath()+($matches.getQuery()!=null?"?"+$matches.getQuery():""):"/";
			if( $matches.getPort()>0 ) $port = $matches.getPort();
		} catch (MalformedURLException e1) {
		}

		
		StringBuffer $out = new StringBuffer();
		if($post!=null && $post.length()>0) {
			$out.append("POST ").append($path).append(" HTTP/1.0\r\n");
			$out.append("Accept: */*\r\n");
			$out.append("Accept-Language: zh-cn\r\n");
			$out.append("Content-Type: application/x-www-form-urlencoded\r\n");
			$out.append("User-Agent: \r\n");
			$out.append("Host: ").append($host).append("\r\n");
			$out.append("Content-Length: ").append($post.length()).append("\r\n");
			$out.append("Connection: Close\r\n");
			$out.append("Cache-Control: no-cache\r\n");
			$out.append("Cookie: \r\n\r\n");
			$out.append($post);
		} else {
			$out.append("GET $path HTTP/1.0\r\n");
			$out.append( "Accept: */*\r\n");
			//$out .= "Referer: $boardurl\r\n";
			$out.append("Accept-Language: zh-cn\r\n");
			$out.append("User-Agent: Java/1.5.0_01\r\n");
			$out.append("Host: $host\r\n");
			$out.append("Connection: Close\r\n");
			$out.append("Cookie: $cookie\r\n\r\n");
		}
	
		try{
			Socket $fp = new Socket($ip!=null && $ip.length()>10? $ip : $host, $port );
			if(!$fp.isConnected()) {
				return "";//note $errstr : $errno \r\n
			} else {

				OutputStream os = $fp.getOutputStream();
				os.write($out.toString().getBytes());
				
				InputStream ins = $fp.getInputStream();				
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
				while (true) {
					String $header = reader.readLine();
					if($header == null || $header.equals("") || $header == "\r\n" || $header == "\n") {
						break;
					}
				}
				
				while (true) {
					String $data = reader.readLine();
					if($data == null || $data.equals("") ) {
						break;
					}else{
						$return+=$data;
					}
				}
				$fp.close();
			}
		}catch (IOException e) {
			
		}
		return $return;
	}
	
	public String uc_app_ls() {
		String $return = call_user_func(UC_API_FUNC, "app", "ls", null);
		return UC_CONNECT.equals("mysql") ? $return : uc_unserialize($return);
	}
	
	/**
	 * 鐢ㄦ埛娉ㄥ唽
	 *
	 * @param string $username 	鐢ㄦ埛鍚�
	 * @param string $password 	瀵嗙爜
	 * @param string $email		Email
	 * @param int $questionid	瀹夊叏鎻愰棶
	 * @param string $answer 	瀹夊叏鎻愰棶绛旀
	 * @return int
		-1 : 鐢ㄦ埛鍚嶄笉鍚堟硶
		-2 : 鍖呭惈涓嶅厑璁告敞鍐岀殑璇嶈
		-3 : 鐢ㄦ埛鍚嶅凡缁忓瓨鍦�
		-4 : email 鏍煎紡鏈夎
		-5 : email 涓嶅厑璁告敞鍐�
		-6 : 璇�email 宸茬粡琚敞鍐�
		>1 : 琛ㄧず鎴愬姛锛屾暟鍊间负 UID
	*/
	public String uc_user_register(String $username, String $password, String $email){
		return uc_user_register($username, $password, $email, "", "");
	}
	public String uc_user_register(String $username, String $password, String $email, String $questionid, String $answer) {
		Map<String ,Object> args = new HashMap<String, Object>();
		args.put("username",$username);
		args.put("password",$password); 
		args.put("email",$email);
		args.put("questionid",$questionid);
		args.put("answer",$answer);
		return call_user_func(UC_API_FUNC, "user", "register", args);
	}
	
	/**
	 * 鐢ㄦ埛鐧婚檰妫�煡
	 *
	 * @param string $username	鐢ㄦ埛鍚�uid
	 * @param string $password	瀵嗙爜
	 * @param int $isuid		鏄惁涓簎id
	 * @param int $checkques	鏄惁浣跨敤妫�煡瀹夊叏闂瓟
	 * @param int $questionid	瀹夊叏鎻愰棶
	 * @param string $answer 	瀹夊叏鎻愰棶绛旀
	 * @return array (uid/status, username, password, email)
	 	鏁扮粍绗竴椤�
	 	1  : 鎴愬姛
		-1 : 鐢ㄦ埛涓嶅瓨鍦�鎴栬�琚垹闄�
		-2 : 瀵嗙爜閿�
	*/
	public String uc_user_login(String $username, String $password){
		return uc_user_login($username, $password, 0, 0);
	}
	public String uc_user_login(String $username, String $password, int $isuid , int $checkques ){
		return uc_user_login($username, $password, $isuid, $checkques, "","");
	}
	public String uc_user_login(String $username, String $password, int $isuid , int $checkques , String $questionid, String $answer) {
		Map<String,Object> args = new HashMap<String, Object>();
		args.put("username",$username);
		args.put("password",$password);
		args.put("isuid",$isuid);
		args.put("checkques",$checkques);
		args.put("questionid", $questionid);
		args.put("answer",$answer);
		String $return = call_user_func(UC_API_FUNC, "user", "login", args);
		return UC_CONNECT .equals("mysql") ? $return : uc_unserialize($return);
	}

	/**
	 * 杩涘叆鍚屾鐧诲綍浠ｇ爜
	 *
	 * @param int $uid		鐢ㄦ埛ID
	 * @return string 		HTML浠ｇ爜
	 */
	public String uc_user_synlogin(int $uid) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("uid",$uid);
		String $return = uc_api_post("user", "synlogin", args);
		return $return;
	}

	/**
	 * 杩涘叆鍚屾鐧诲嚭浠ｇ爜
	 *
	 * @return string 		HTML浠ｇ爜
	 */
	public String uc_user_synlogout() {
		String $return = uc_api_post("user", "synlogout", new HashMap<String, Object>());
		return $return;
	}

	/**
	 * 鍙栧緱鐢ㄦ埛鏁版嵁
	 *
	 * @param string $username	鐢ㄦ埛鍚�
	 * @param int $isuid	鏄惁涓篣ID
	 * @return array (uid, username, email)
	 */
	public String uc_get_user(String $username, int $isuid) {
		Map<String,Object> args = new HashMap<String, Object>();
		args.put("username",$username );
		args.put("isuid", $isuid );
		String $return = call_user_func(UC_API_FUNC, "user", "get_user", args);
		return UC_CONNECT.equals("mysql") ? $return : uc_unserialize($return);
	}
	/**
	 * 缂栬緫鐢ㄦ埛
	 *
	 * @param string $username	鐢ㄦ埛鍚�
	 * @param string $oldpw		鏃у瘑鐮�
	 * @param string $newpw		鏂板瘑鐮�
	 * @param string $email		Email
	 * @param int $ignoreoldpw 	鏄惁蹇界暐鏃у瘑鐮� 蹇界暐鏃у瘑鐮� 鍒欎笉杩涜鏃у瘑鐮佹牎楠�
	 * @param int $questionid	瀹夊叏鎻愰棶
	 * @param string $answer 	瀹夊叏鎻愰棶绛旀
	 * @return int
	 	1  : 淇敼鎴愬姛
	 	0  : 娌℃湁浠讳綍淇敼
	  	-1 : 鏃у瘑鐮佷笉姝ｇ‘
		-4 : email 鏍煎紡鏈夎
		-5 : email 涓嶅厑璁告敞鍐�
		-6 : 璇�email 宸茬粡琚敞鍐�
		-7 : 娌℃湁鍋氫换浣曚慨鏀�
		-8 : 鍙椾繚鎶ょ殑鐢ㄦ埛锛屾病鏈夋潈闄愪慨鏀�
	*/
	public String uc_user_edit(String $username, String $oldpw, String $newpw, String $email, int $ignoreoldpw, String $questionid, String $answer) {
		Map<String,Object> args = new HashMap<String, Object>();		 
		args.put("username", $username);
		args.put("oldpw",$oldpw);
		args.put("newpw",$newpw);
		args.put("email",$email);
		args.put("ignoreoldpw",$ignoreoldpw);
		args.put("questionid", $questionid);
		args.put("answer", $answer);
		return call_user_func(UC_API_FUNC, "user", "edit", args);
	}

	/**
	 * 鍒犻櫎鐢ㄦ埛
	 *
	 * @param string/array $uid	鐢ㄦ埛鐨�UID
	 * @return int
	 	>0 : 鎴愬姛
	 	0 : 澶辫触
	 */
	public String uc_user_delete(String $uid) {
		Map<String,Object> args = new HashMap<String, Object>();	
		args.put("uid",$uid);
		return call_user_func(UC_API_FUNC, "user", "delete", args);
	}

	/**
	 * 鍒犻櫎鐢ㄦ埛澶村儚
	 *
	 * @param string/array $uid	鐢ㄦ埛鐨�UID
	 */
	public String uc_user_deleteavatar(String $uid) {
		Map<String,Object> args = new HashMap<String, Object>();	
		args.put("uid",$uid);
		return uc_api_post("user", "deleteavatar", args);
	}

	
}
