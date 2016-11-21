package com.sobey.cloud.webtv.bean;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonObjectResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.utils.MConfig;
import com.sobey.cloud.webtv.utils.PreferencesUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

/**
 * 负责圈子相关接口的解析类
 * 
 * @author bin
 *
 */
public class GroupRequestMananger {

	public static GroupRequestMananger groupRequestMananger = null;
	public SobeyBaseResult baseResult = null;
	private final String TAG = this.getClass().getName();

	private GroupRequestMananger() {

		baseResult = new SobeyBaseResult();
	}

	public static synchronized GroupRequestMananger getInstance() {
		if (null == groupRequestMananger) {
			groupRequestMananger = new GroupRequestMananger();
		}

		return groupRequestMananger;
	}

	/**
	 * 获取圈子列表
	 * 
	 * @param uid
	 *            用户id
	 * @param ctx
	 * @param listener
	 */
	public void getGroupList(Context ctx, final RequestResultListner listener) {

		// 读取缓存文件
		// String buffer = null;
		// if(FileUtil.hasStorage()){
		// buffer = FileUtil.readTextFile(SobeyFileNameConstants.QUANZI_CACHE);
		// }else{
		// buffer =
		// FileUtil.readTextFromDataDir(SobeyFileNameConstants.FileName.quanzi.name());
		// }
		// try {
		// JSONObject result = new JSONObject(buffer);
		// listener.onFinish(RequestResultParser.parseGroupsModel(result));
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }
		//
		// //如果没有网络不再发出网络请求
		// if(!(new CheckNetwork(ctx).getNetworkState(false)))
		// return;
		String uid = PreferencesUtil.getLoggedUserId();

		News.getGroupList(uid, ctx, new OnJsonObjectResultListener() {

			@Override
			public void onOK(JSONObject result) {

				listener.onFinish(RequestResultParser.parseGroupsModel(result));
				// if(FileUtil.hasStorage()){
				// FileUtil.saveTextFile(SobeyFileNameConstants.QUANZI_CACHE,
				// result.toString());
				// }else{
				// FileUtil.saveTextDataDir(SobeyFileNameConstants.FileName.quanzi.name(),
				// result.toString());
				// }
			}

			@Override
			public void onNG(String reason) {

				baseResult.resultInfo = reason;
				listener.onFinish(baseResult);

			}

			@Override
			public void onCancel() {

				baseResult.resultInfo = "cancel";
				listener.onFinish(baseResult);

			}
		});

	}

	/**
	 * 关注圈子
	 * 
	 * @param uid
	 *            用户id
	 * @param follow
	 *            1-关注，0-取消关注
	 * @param groupId
	 *            圈子id
	 * @param ctx
	 * @param listener
	 */
	public void followGroup(int follow, String groupId, Context ctx, final RequestResultListner listener) {
		String uid = PreferencesUtil.getLoggedUserId();
		News.followGroup(uid, follow, groupId, ctx, new MyOnJsonObjectResultListener(listener) {
			@Override
			public void onOK(JSONObject result) {
				super.onOK(result);
				listener.onFinish(RequestResultParser.parseBaseResult(result));
			}
		});
	}

	/**
	 * 收藏帖子
	 * 
	 * @param subjectId
	 *            帖子Id
	 * @param isCollect
	 *            1-收藏 0-取消收藏
	 * @param ctx
	 * @param listener
	 */
	public void collectSubject(String subjectId, int isCollect, Context ctx, final RequestResultListner listener) {
		String uid = PreferencesUtil.getLoggedUserId();
		News.collectSubject(uid, subjectId, isCollect, ctx, new MyOnJsonObjectResultListener(listener) {
			@Override
			public void onOK(JSONObject result) {
				super.onOK(result);
				listener.onFinish(RequestResultParser.parseBaseResult(result));
			}
		});
	}

	public enum SearchType {
		searchGroup, searchUser, searchSubject
	}

	/**
	 * 搜索
	 * 
	 * @param searchType
	 *            搜索类型
	 * @param keyWord
	 *            搜索关键词
	 * @param ctx
	 * @param listener
	 */
	public void search(final SearchType searchType, String keyWord, int page, Context ctx,
			final RequestResultListner listener) {
		News.search(searchType.name(), keyWord, page, ctx, new OnJsonObjectResultListener() {

			@Override
			public void onOK(JSONObject result) {
				SearchResult searchResult = new SearchResult();
				switch (searchType) {
				case searchGroup:

					if (null != result) {
						JSONArray array = result.optJSONArray("otherGroupList");
						searchResult.groupModels = RequestResultParser.parseListGroupModel(array, -1);
					}

					break;
				case searchUser:

					if (null != result) {
						JSONArray array = result.optJSONArray("users");
						searchResult.userModels = RequestResultParser.parseListUserModel(array);
					}

					break;
				case searchSubject:

					if (null != result) {
						JSONArray array = result.optJSONArray("subjectList");
						searchResult.subjectModels = RequestResultParser.parseListSubjectModel(array);
					}

					break;

				default:
					break;
				}
				listener.onFinish(searchResult);

			}

			@Override
			public void onNG(String reason) {
				baseResult.resultInfo = reason;
				listener.onFinish(baseResult);
			}

			@Override
			public void onCancel() {
				baseResult.resultInfo = "cancel";
				listener.onFinish(baseResult);
			}
		});
	}

	/**
	 * 获取用户信息
	 * 
	 * @param uid
	 *            用户id
	 * @param ctx
	 * @param listener
	 */
	// public void getGroupUserInfo(Context ctx,
	// final RequestResultListner listener) {
	// String uid = PreferencesUtil.getLoggedUserId();
	// String cid = PreferencesUtil.getLoggedUserId();
	// if(TextUtils.isEmpty(cid)){
	// cid = uid;
	// }
	// News.getGroupUserInfo(uid, ctx, new OnJsonObjectResultListener() {
	//
	// @Override
	// public void onOK(JSONObject result) {
	// if (null != result) {
	// listener.onFinish(RequestResultParser
	// .parseUserModel(result));
	// }
	// }
	//
	// @Override
	// public void onNG(String reason) {
	// baseResult.resultInfo = reason;
	// listener.onFinish(baseResult);
	// }
	//
	// @Override
	// public void onCancel() {
	// baseResult.resultInfo = "cancel";
	// listener.onFinish(baseResult);
	// }
	// });
	// }

	/**
	 * 获取圈子详情
	 * 
	 * @param groupId
	 *            圈子id
	 * @param ctx
	 * @param listener
	 */
	public void getGroupInfo(String groupId, int page, String sort, String filter, final Context ctx,
			final RequestResultListner listener) {
		String uid = PreferencesUtil.getLoggedUserId();
		News.getGroupInfo(uid, groupId, page, sort, filter, ctx, new OnJsonObjectResultListener() {

			@Override
			public void onOK(JSONObject result) {
				// 2015.10.19 他妈的 返回的数据里有“<img id="aimg_q6sQI"
				// onclick="zoom(this, this.src, 0, 0, 0)" class="zoom"
				// width="600"
				// src="http://webtv.sobeycloud.com/hlt//upload/Image/mrtp/3668c1ec2def491cb5a280928f678199.jpg"
				// onmouseover="img_onmouseoverfunc(this)" onclick="zoom(this)"
				// style="cursor:pointer" border="0" alt=""
				// />”这种狗屎东西，球JB用没得，解析还要出错，直接过滤掉
				// Pattern pattern = Pattern.compile("<img(.*)/>");
				// Matcher matcher = pattern.matcher(result.toString());
				// String filterResult = matcher.replaceAll("");
				// try {
				// result = new JSONObject(filterResult);
				// } catch (JSONException e) {
				// e.printStackTrace();
				// }
				if (null != result) {
					listener.onFinish(RequestResultParser.parseGroupModel(result));
				}
			}

			@Override
			public void onNG(String reason) {
				baseResult.resultInfo = reason;
				listener.onFinish(baseResult);
			}

			@Override
			public void onCancel() {
				baseResult.resultInfo = "cancel";
				listener.onFinish(baseResult);
			}
		});
	}

	/**
	 * 获取主题详情
	 * 
	 * @param subjectId
	 *            主题id
	 * @param ctx
	 * @param listener
	 */
	public void getGroupSubjectInfo(String subjectId, int page, String filter, Context ctx,
			final RequestResultListner listener) {
		String uid = PreferencesUtil.getLoggedUserId();
		News.getGroupSubjectInfo(uid, subjectId, page, filter, ctx, new MyOnJsonObjectResultListener(listener) {

			@Override
			public void onOK(JSONObject result) {
				if (null != result) {
					listener.onFinish(RequestResultParser.parseSubjectModel(result));
				}
			}
		});
	}

	/**
	 * 主题点赞
	 * 
	 * @param uid
	 * @param subjectId
	 * @param like
	 * @param ctx
	 * @param listener
	 */
	public void likeSubject(String subjectId, int like, Context ctx, final RequestResultListner listener) {
		String uid = PreferencesUtil.getLoggedUserId();
		News.likeSubject(uid, subjectId, like, ctx, new MyOnJsonObjectResultListener(listener) {

			@Override
			public void onOK(JSONObject result) {
				if (null != result) {
					listener.onFinish(RequestResultParser.parseBaseResult(result));
				}
			}

		});
	}

	public void likeComment(String commentId, int like, Context ctx, final RequestResultListner listener) {
		String uid = PreferencesUtil.getLoggedUserId();
		News.likeComment(uid, commentId, like, ctx, new MyOnJsonObjectResultListener(listener) {

			@Override
			public void onOK(JSONObject result) {
				if (null != result) {
					listener.onFinish(RequestResultParser.parseBaseResult(result));
				}
			}

		});
	}

	/**
	 * 发布主题
	 * 
	 * @param groupId
	 * @param subjectTitle
	 * @param subjectContent
	 * @param subjectPics
	 * @param linkFriends
	 * @param locationInfo
	 * @param ctx
	 * @param listener
	 */
	public void postSubject(String groupId, String subjectTitle, String subjectContent, String subjectPics,
			String linkFriends, String locationInfo, Context ctx, final RequestResultListner listener) {
		String uid = PreferencesUtil.getLoggedUserId();
		News.postSubject(uid, groupId, subjectTitle, subjectContent, subjectPics, linkFriends, locationInfo, ctx,
				new MyOnJsonObjectResultListener(listener) {
					@Override
					public void onOK(JSONObject result) {
						super.onOK(result);
						if (null != result) {
							listener.onFinish(RequestResultParser.parseBaseResult(result));
						}
					}
				});
	}

	/**
	 * 发布评论
	 * 
	 * @param subjectId
	 * @param commentContent
	 * @param commentPics
	 * @param ctx
	 * @param listener
	 */
	public void postComment(String subjectId, String linkFrienduid, String commentContent, String commentPics,
			Context ctx, final RequestResultListner listener) {
		String uid = PreferencesUtil.getLoggedUserId();
		News.postComment(uid, subjectId,linkFrienduid, commentContent, commentPics, ctx, new MyOnJsonObjectResultListener(listener) {
			@Override
			public void onOK(JSONObject result) {
				super.onOK(result);
				if (null != result) {
					listener.onFinish(RequestResultParser.parseBaseResult(result));
				}
			}
		});
	}

	/**
	 * 发布回复接口
	 * 
	 * @param subjectId
	 *            当前主题的id
	 * @param commentId
	 *            当前评论的id
	 * @param replyUserId
	 *            回复人的id
	 * @param replyContent
	 *            回复的内容
	 * @param replyPics
	 *            图片地址
	 * @param ctx
	 * @param listener
	 */
	public void postReply(String subjectId, String commentId, String replyUserId, String replyContent, String replyPics,
			String linkFriends, String locationInfo, Context ctx, final RequestResultListner listener) {
		News.postReply(subjectId, commentId, replyUserId, replyContent, replyPics, linkFriends, locationInfo, ctx,
				new MyOnJsonObjectResultListener(listener) {
					@Override
					public void onOK(JSONObject result) {
						super.onOK(result);
						if (null != result) {
							listener.onFinish(RequestResultParser.parseBaseResult(result));
						}
					}
				});
	}

	public void getUserInfo(String uid, Context ctx, RequestResultListner listener) {
		String cid = PreferencesUtil.getLoggedUserId();
		if (TextUtils.isEmpty(cid)) {
			cid = uid;
		}
		News.getGroupUserInfo(uid, cid, ctx, new MyOnJsonObjectResultListener(listener) {
			@Override
			public void onOK(JSONObject result) {
				super.onOK(result);
				if (null != result) {
					listener.onFinish(RequestResultParser.parseUserModel(result));
				}
			}
		});
	}

	/**
	 * 获取我的帖子 2015/12/7
	 * 
	 * @param uid
	 * @param ctx
	 * @param listener
	 */
	public void getUserTiezi(String uid, Context ctx, RequestResultListner listener) {
		String cid = PreferencesUtil.getLoggedUserId();
		if (TextUtils.isEmpty(cid)) {
			cid = uid;
		}
		News.getGroupUserTiezi(uid, cid, ctx, new MyOnJsonObjectResultListener(listener) {
			@Override
			public void onOK(JSONObject result) {
				super.onOK(result);
				if (null != result) {
					listener.onFinish(RequestResultParser.parseUserModel(result));
				}
			}
		});
	}

	public void getUserMesasgeList(int page, Context ctx, RequestResultListner listener) {
		String uid = PreferencesUtil.getLoggedUserId();
		News.getUserMessageList(uid, page, ctx, new MyOnJsonObjectResultListener(listener) {
			@Override
			public void onOK(JSONObject result) {
				super.onOK(result);
				listener.onFinish(RequestResultParser.parseMsgListResult(result));
			}
		});
	}

	public void getUserPrivateLetterList(int page, Context ctx, RequestResultListner listener) {
		String uid = PreferencesUtil.getLoggedUserId();
		News.getUserPrivateLetterList(uid, page, ctx, new MyOnJsonObjectResultListener(listener) {
			@Override
			public void onOK(JSONObject result) {
				super.onOK(result);
				listener.onFinish(RequestResultParser.parseLetterListResult(result));
			}
		});
	}

	/**
	 * 关注用户
	 * 
	 * @param toUid
	 *            目标用户uid
	 * @param follow
	 *            0-取消关注 1-关注
	 * @param ctx
	 * @param listener
	 */
	public void followUser(String toUid, int follow, Context ctx, RequestResultListner listener) {
		String uid = PreferencesUtil.getLoggedUserId();
		News.followUser(uid, toUid, follow, ctx, new MyOnJsonObjectResultListener(listener) {
			@Override
			public void onOK(JSONObject result) {
				super.onOK(result);
				listener.onFinish(RequestResultParser.parseLetterListResult(result));
			}
		});
	}

	/**
	 * 关注用户
	 * 
	 * @param toUid
	 *            目标用户uid
	 * @param follow
	 *            0-取消关注 1-关注
	 * @param ctx
	 * @param listener
	 */
	public void modifyUserInfo(String nickName, String sex, String email, Context ctx, RequestResultListner listener) {
		String uid = PreferencesUtil.getLoggedUserId();
		SharedPreferences settings = ctx.getSharedPreferences("user_info", 0);
		String tel = settings.getString("telphone", "");
		News.modifyUserInfo(uid, nickName, sex, email, tel, ctx, new MyOnJsonObjectResultListener(listener) {
			@Override
			public void onOK(JSONObject result) {
				super.onOK(result);
				listener.onFinish(RequestResultParser.parseBaseResult(result));
			}
		});
	}

	/**
	 */
	public void postPrivateLetter(String toUid, String letterTitle, String letterContent) {

	}

	public String sendHttpMultipartRequest(List<File> fs) {

		Map<String, String> requestParam = new HashMap<String, String>();
		requestParam.put("action", "uploadImage");
		requestParam.put("uid", "1");

		String result = null;
		int blockSize = 1024;
		String boundary = "---wisonic---";
		try {
			// 打开连接
			URL requestUrl = new URL("http://qz.ieator.com/plugin.php?id=sobeyapp:api");
			HttpURLConnection httpConn = (HttpURLConnection) requestUrl.openConnection();
			httpConn.addRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			httpConn.setRequestProperty("connection", "Keep-Alive");
			httpConn.setRequestProperty("Accept",
					"image/gif,image/x-xbitmap,image/jpeg,image/pjpeg,application/vnd.ms-excel,application/vnd.ms-powerpoint,   application/msword,   application/x-shockwave-flash,application/x-quickviewplus,*/*");

			httpConn.setRequestMethod("POST");
			httpConn.setDoOutput(true);
			httpConn.setChunkedStreamingMode(blockSize);

			DataOutputStream out = new DataOutputStream(httpConn.getOutputStream());
			StringBuilder sb = new StringBuilder();
			for (String key : requestParam.keySet()) {
				sb.append("--").append(boundary).append("\r\n");
				sb.append("Content-Disposition: form-data; name=\"").append(key).append("\"").append("\"\r\n\r\n");
				sb.append(URLEncoder.encode(requestParam.get(key), "utf-8"));
				sb.append("\r\n--").append(boundary).append("\r\n");
			}

			out.write(sb.toString().getBytes());

			for (int i = 0; i < fs.size(); i++) {
				out.writeBytes("--" + boundary + "\r\n");
				out.writeBytes("Content-Disposition: form-data; name=\"" + "Filedata" + "\"; filename=\""
						+ URLEncoder.encode(fs.get(i).getName(), "utf-8") + "\"\r\n");
				out.writeBytes("Content-Type: application/octet-stream\r\n");
				out.writeBytes("\r\n");

				FileInputStream fis = new FileInputStream(fs.get(i)); // 读取文件内容，发送数据，数据要一点点发送，不能一下全部读取发送，否则会内存溢出。
				int rn;
				byte[] buf = new byte[blockSize];
				while ((rn = fis.read(buf, 0, blockSize)) > 0) {
					out.write(buf, 0, rn);
				}
				fis.close();
				byte[] endData = ("\r\n--" + boundary + "--\r\n").getBytes();
				out.write(endData);
			}
			out.flush();
			out.close();
			// 获取输入流
			BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
			int code = httpConn.getResponseCode();
			if (HttpsURLConnection.HTTP_OK == code) {
				String temp = in.readLine();
				/* 连接成一个字符串 */
				while (temp != null) {
					if (result != null)
						result += temp;
					else
						result = temp;
					temp = in.readLine();
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public String sendHttpMultipartRequest(File file) {

		Log.i("upLoadFile", "正在上传--->");
		Log.i("upLoadFile", "要上传的文件路径：" + file.getName());
		// Map<String, String> requestParam = new HashMap<String, String>();
		// requestParam.put("action", "uploadImage");
		String uid = PreferencesUtil.getLoggedUserId();
		// requestParam.put("uid", uid);
		Log.i("upLoadFile", "上传用户的uid:--->" + uid);
		String result = null;
		int blockSize = 1024;
		String boundary = "---wisonic---";
		try {
			// 打开连接
			// URL requestUrl = new
			// URL("http://qz.ieator.com/plugin.php?id=sobeyapp:api");
			StringBuilder sb = new StringBuilder();
			sb.append(MConfig.mQuanZiApiUrl).append("&").append("action").append("=").append("uploadImage").append("&")
					.append("uid").append("=").append(uid);

			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {

				System.setProperty("http.keepAlive", "false");

			}

			URL requestUrl = new URL(sb.toString());// http://qz.sobeycache.com/plugin.php?id=sobeyapp:api
			HttpURLConnection httpConn = (HttpURLConnection) requestUrl.openConnection();
			httpConn.addRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			httpConn.setRequestProperty("connection", "Keep-Alive");
			httpConn.setRequestProperty("Accept",
					"image/gif,image/x-xbitmap,image/jpeg,image/pjpeg,application/vnd.ms-excel,application/vnd.ms-powerpoint,   application/msword,   application/x-shockwave-flash,application/x-quickviewplus,*/*");

			httpConn.setRequestMethod("POST");
			httpConn.setDoOutput(true);
			httpConn.setChunkedStreamingMode(blockSize);

			DataOutputStream out = new DataOutputStream(httpConn.getOutputStream());
			// StringBuilder sb = new StringBuilder();
			// for (String key : requestParam.keySet()) {
			// sb.append("--").append(boundary).append("\r\n");
			// sb.append("Content-Disposition: form-data; name=\"")
			// .append(key).append("\"").append("\"\r\n\r\n");
			// sb.append(URLEncoder.encode(requestParam.get(key), "utf-8"));
			// sb.append("\r\n--").append(boundary).append("\r\n");
			// }
			//
			// out.write(sb.toString().getBytes());

			out.writeBytes("--" + boundary + "\r\n");
			out.writeBytes("Content-Disposition: form-data; name=\"" + "Filedata" + "\"; filename=\""
					+ URLEncoder.encode(file.getName(), "utf-8") + "\"\r\n");
			out.writeBytes("Content-Type: application/octet-stream\r\n");
			out.writeBytes("\r\n");

			FileInputStream fis = new FileInputStream(file); // 读取文件内容，发送数据，数据要一点点发送，不能一下全部读取发送，否则会内存溢出。
			int rn;
			byte[] buf = new byte[blockSize];
			while ((rn = fis.read(buf, 0, blockSize)) > 0) {
				out.write(buf, 0, rn);
			}
			fis.close();
			byte[] endData = ("\r\n--" + boundary + "--\r\n").getBytes();
			out.write(endData);

			out.flush();
			out.close();
			// 获取输入流
			BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
			int code = httpConn.getResponseCode();
			if (HttpsURLConnection.HTTP_OK == code) {
				String temp = in.readLine();
				/* 连接成一个字符串 */
				while (temp != null) {
					if (result != null)
						result += temp;
					else
						result = temp;
					temp = in.readLine();
				}
			}
		} catch (MalformedURLException e) {
			Log.i("upLoadFile", "上传发生异常:--->" + e.getMessage());
			e.printStackTrace();
		} catch (ProtocolException e) {
			Log.i("upLoadFile", "上传发生异常:--->" + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.i("upLoadFile", "上传发生异常:--->" + e.getMessage());
			e.printStackTrace();
		}
		Log.i("upLoadFile", "上传返回结果：" + result);
		return result;
	}

	private class MyOnJsonObjectResultListener implements OnJsonObjectResultListener {
		RequestResultListner listener;

		MyOnJsonObjectResultListener(RequestResultListner listener) {
			this.listener = listener;
		}

		@Override
		public void onOK(JSONObject result) {
		}

		@Override
		public void onNG(String reason) {
			Log.i(TAG, "onNG-->" + reason);
			baseResult.resultInfo = reason;
			listener.onFinish(baseResult);
		}

		@Override
		public void onCancel() {
			Log.i(TAG, "onCancel-->");
			baseResult.resultInfo = "cancel";
			listener.onFinish(baseResult);
		}

	}

	/**
	 * 获取圈子列表结果监听
	 * 
	 * @author DZY
	 *
	 */
	public interface RequestResultListner {

		void onFinish(SobeyType result);
	}
}
