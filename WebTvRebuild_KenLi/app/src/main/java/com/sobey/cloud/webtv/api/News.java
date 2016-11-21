package com.sobey.cloud.webtv.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONObject;

import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonArrayResultListener;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonObjectResultListener;
import com.sobey.cloud.webtv.utils.MConfig;
import com.sobey.cloud.webtv.utils.PreferencesUtil;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

public class News {
	public static JSONArray getCatalogList(String catalogId, int getAllData, Context ctx,
			OnJsonArrayResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "getCatalogList"));
			param.add(new BasicNameValuePair("siteId", String.valueOf(MConfig.SITE_ID)));
			if (catalogId != null) {
				param.add(new BasicNameValuePair("catalogId", catalogId));
			}
			param.add(new BasicNameValuePair("getAllData", String.valueOf(getAllData)));
			if (listener == null) {
				return HttpInvoke.invokePostJsonArray(param);
			} else {
				HttpInvoke.asyncInvokePostJsonArray(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	/**
	 * 光电新闻接口
	 * 
	 * @param isTop
	 * @param catalogId
	 * @param limit
	 * @param index
	 * @param ctx
	 * @param listener
	 * @return
	 */
	public static JSONObject getArticleList(int isTop, String catalogId, int limit, int index, Context ctx,
			OnJsonObjectResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "getArticleList"));// 之前是getSiteTop
			param.add(new BasicNameValuePair("siteId", String.valueOf(MConfig.SITE_ID)));
			param.add(new BasicNameValuePair("isTop", String.valueOf(isTop)));
			if (catalogId != null) {
				param.add(new BasicNameValuePair("catalogId", catalogId));
			}
			param.add(new BasicNameValuePair("pageNum", String.valueOf(index)));
			param.add(new BasicNameValuePair("pageSize", String.valueOf(limit)));
			param.add(new BasicNameValuePair("sortField", "PublishDate"));
			param.add(new BasicNameValuePair("sort", "DESC"));
			if (listener == null) {
				return HttpInvoke.invokePostJsonObject(param);
			} else {
				HttpInvoke.asyncInvokePostJsonObject(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	/**
	 * 获取直播新闻
	 * 
	 * @param catalogId
	 * @param limit
	 * @param index
	 * @param ctx
	 * @param listener
	 * @return
	 */
	public static JSONObject getArticleList4Live(String catalogId, int limit, int index, Context ctx,
			OnJsonObjectResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "getArticleList"));
			param.add(new BasicNameValuePair("siteId", String.valueOf(MConfig.SITE_ID)));
			if (catalogId != null) {
				param.add(new BasicNameValuePair("catalogId", catalogId));
			}
			param.add(new BasicNameValuePair("pageNum", String.valueOf(index)));
			param.add(new BasicNameValuePair("pageSize", String.valueOf(limit)));
			if (listener == null) {
				return HttpInvoke.invokePostJsonObject(param);
			} else {
				HttpInvoke.asyncInvokePostJsonObject(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONArray getArticleById(String articleId, String catalogId, String username, String terminalType,
			String actDate, Context ctx, OnJsonArrayResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "getArticleById"));
			param.add(new BasicNameValuePair("siteId", String.valueOf(MConfig.SITE_ID)));
			param.add(new BasicNameValuePair("articleId", articleId));
			if (!TextUtils.isEmpty(catalogId)) {
				param.add(new BasicNameValuePair("catalogId", catalogId));
			}
			param.add(new BasicNameValuePair("username", username));
			if (terminalType != null) {
				param.add(new BasicNameValuePair("TerminalType", terminalType));
			}
			if (actDate != null) {
				param.add(new BasicNameValuePair("actDate", actDate));
			}
			if (listener == null) {
				return HttpInvoke.invokePostJsonArray(param);
			} else {
				HttpInvoke.asyncInvokePostJsonArray(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null) {
				listener.onNG(ex.toString());
			}
			return null;
		}
	}

	public static JSONObject getRelativeArticle(String articleId, Context ctx, OnJsonObjectResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "getRelativeArticle"));
			param.add(new BasicNameValuePair("siteId", String.valueOf(MConfig.SITE_ID)));
			param.add(new BasicNameValuePair("articleId", articleId));
			if (listener == null) {
				return HttpInvoke.invokePostJsonObject(param);
			} else {
				HttpInvoke.asyncInvokePostJsonObject(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONObject getVideoPath(String videoPath, Context ctx, OnJsonObjectResultListener listener) {
		try {
			if (listener == null) {
				return HttpInvoke.invokeGetJsonObject(videoPath);
			} else {
				HttpInvoke.asyncInvokeGetJsonObject(videoPath, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONArray getTvGuide(String guidePath, Context ctx, OnJsonArrayResultListener listener) {
		try {
			if (listener == null) {
				return HttpInvoke.invokeGetJsonArray(guidePath);
			} else {
				HttpInvoke.asyncInvokeGetJsonArray(guidePath, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONArray getCommentByArticleId(String articleId, String catalogId, Context ctx,
			OnJsonArrayResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "getCommentByArticleId"));
			param.add(new BasicNameValuePair("siteId", String.valueOf(MConfig.SITE_ID)));
			param.add(new BasicNameValuePair("articleId", articleId));
			param.add(new BasicNameValuePair("catalogId", catalogId));
			param.add(new BasicNameValuePair("verifyFlag", "1"));
			if (listener == null) {
				return HttpInvoke.invokePostJsonArray(param);
			} else {
				HttpInvoke.asyncInvokePostJsonArray(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONArray addComment(String title, String catalogId, String catalogType, String articleId,
			String commentUser, String content, String ip, Context ctx, OnJsonArrayResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "addComment"));
			param.add(new BasicNameValuePair("siteId", String.valueOf(MConfig.SITE_ID)));
			param.add(new BasicNameValuePair("Title", title));
			param.add(new BasicNameValuePair("catalogId", catalogId));
			param.add(new BasicNameValuePair("catalogType", catalogType));
			param.add(new BasicNameValuePair("articleId", articleId));
			param.add(new BasicNameValuePair("commentUser", commentUser));
			param.add(new BasicNameValuePair("content", content));
			param.add(new BasicNameValuePair("ip", ip));
			if (listener == null) {
				return HttpInvoke.invokePostJsonArray(param);
			} else {
				HttpInvoke.asyncInvokePostJsonArray(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONArray getHotword(int top, Context ctx, OnJsonArrayResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "getHotword"));
			param.add(new BasicNameValuePair("siteId", String.valueOf(MConfig.SITE_ID)));
			param.add(new BasicNameValuePair("top", String.valueOf(top)));
			if (listener == null) {
				return HttpInvoke.invokePostJsonArray(param);
			} else {
				HttpInvoke.asyncInvokePostJsonArray(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONObject searchArticleList(int isTop, String keyword, int type, Context ctx,
			OnJsonObjectResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "getArticleList"));
			param.add(new BasicNameValuePair("siteId", String.valueOf(MConfig.SITE_ID)));
			param.add(new BasicNameValuePair("isTop", String.valueOf(isTop)));
			if (type != 0) {
				param.add(new BasicNameValuePair("type", String.valueOf(type)));
			}
			param.add(new BasicNameValuePair("keywords", keyword));
			param.add(new BasicNameValuePair("sortField", "PublishDate"));
			param.add(new BasicNameValuePair("sort", "DESC"));
			if (listener == null) {
				return HttpInvoke.invokePostJsonObject(param);
			} else {
				HttpInvoke.asyncInvokePostJsonObject(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONArray register(String username, String password, Context ctx,
			OnJsonArrayResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "register"));
			param.add(new BasicNameValuePair("siteId", String.valueOf(MConfig.SITE_ID)));
			param.add(new BasicNameValuePair("userName", username));
			param.add(new BasicNameValuePair("Password", password));
			if (listener == null) {
				return HttpInvoke.invokePostJsonArray(param);
			} else {
				HttpInvoke.asyncInvokePostJsonArray(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONArray login(String username, String password, Context ctx, OnJsonArrayResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "verify"));
			param.add(new BasicNameValuePair("siteId", String.valueOf(MConfig.SITE_ID)));
			param.add(new BasicNameValuePair("userName", username));
			param.add(new BasicNameValuePair("Password", password));
			if (listener == null) {
				return HttpInvoke.invokePostJsonArray(param);
			} else {
				HttpInvoke.asyncInvokePostJsonArray(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONArray getUserInfo(String username, Context ctx, OnJsonArrayResultListener listener) {
		try {
			StringBuilder builder = new StringBuilder();
			builder.append("http://uc.sobeycache.com/interface").append("?").append("method").append("=")
					.append("getUserInfo").append("&").append("siteId=" + MConfig.SITE_ID).append("&userName=")
					.append(username);
			String url = builder.toString();
			if (listener == null) {
				return new JSONArray("[" + HttpInvoke.invokeGetJsonObject(url).toString() + "]");
			} else {
				HttpInvoke.asyncInvokeGetJsonObject(url, ctx, listener);
				return null;
			}

		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	// TODO
	public static JSONArray editUserInfo(String username, String password, Context ctx,
			OnJsonArrayResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "verify"));
			param.add(new BasicNameValuePair("siteId", String.valueOf(MConfig.SITE_ID)));
			param.add(new BasicNameValuePair("userName", username));
			param.add(new BasicNameValuePair("Password", password));

			if (listener == null) {
				return HttpInvoke.invokePostJsonArray(param);
			} else {
				HttpInvoke.asyncInvokePostJsonArray(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONArray editUserInfo(String username, String nickname, String sex, String email, String logo,
			String telPhone, Context ctx, OnJsonArrayResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("upload", "upload"));// 不是参数，用于标识是上传信息
			param.add(new BasicNameValuePair("method", "editUserInfo"));
			param.add(new BasicNameValuePair("siteId", String.valueOf(MConfig.SITE_ID)));
			param.add(new BasicNameValuePair("userName", username));
			param.add(new BasicNameValuePair("Email", email));
			param.add(new BasicNameValuePair("nickName", nickname));
			param.add(new BasicNameValuePair("Sex", sex));
			param.add(new BasicNameValuePair("uid", PreferencesUtil.getLoggedUserId()));
			param.add(new BasicNameValuePair("Telphone", telPhone));
			param.add(new BasicNameValuePair("Head", logo));
			if (listener == null) {
				return HttpInvoke.invokePostJsonArray(param);
			} else {
				HttpInvoke.asyncInvokePostJsonArray(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
		// StringBuilder builder = new StringBuilder();
		// builder.append("http://uc.sobeycache.com/interface?method=editUserInfo&siteId=21&userName=")
		// .append(username).append("&Email=").append(email).append("&nickName=").append(nickname)
		// .append("&Sex=").append(sex).append("&uid=").append(PreferencesUtil.getLoggedUserId())
		// .append("&Telphone=" +
		// telPhone).append("&Head=").append(URLEncoder.encode(logo, "utf-8"));
		// String url = builder.toString();
		// if (listener == null) {
		// return new JSONArray("[" +
		// HttpInvoke.invokeGetJsonObject(url).toString() + "]");
		// } else {
		// HttpInvoke.asyncInvokeGetJsonObject(url, ctx, listener);
		// return null;
		// }
		//
		// } catch (Exception ex) {
		// if (listener != null)
		// listener.onNG(ex.toString());
		// return null;
		// }
	}

	public static JSONArray getCommentByMemberName(String username, Context ctx, OnJsonArrayResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "getCommentByMemberName"));
			param.add(new BasicNameValuePair("siteId", String.valueOf(MConfig.SITE_ID)));
			param.add(new BasicNameValuePair("memberName", username));
			if (listener == null) {
				return HttpInvoke.invokePostJsonArray(param);
			} else {
				HttpInvoke.asyncInvokePostJsonArray(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONArray isUserExist(String username, Context ctx, OnJsonArrayResultListener listener) {
		try {
			StringBuilder builder = new StringBuilder();
			builder.append("http://uc.sobeycache.com/interface")
					.append("?method=verifyUser&siteId=" + MConfig.SITE_ID + "&userName=").append(username);
			String url = builder.toString();
			if (listener == null) {
				return new JSONArray("[" + HttpInvoke.invokeGetJsonObject(url).toString() + "]");
			} else {
				HttpInvoke.asyncInvokeGetJsonObject(url, ctx, listener);
				return null;
			}

		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static void increaseHitCount(final String type, final String catalogId, final String articalId) {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					List<NameValuePair> param = new ArrayList<NameValuePair>();
					param.add(new BasicNameValuePair("SiteID", String.valueOf(MConfig.SITE_ID)));
					if (TextUtils.isEmpty(type)) {
						param.add(new BasicNameValuePair("Type", "Article"));
					} else {
						param.add(new BasicNameValuePair("Type", type));
					}
					param.add(new BasicNameValuePair("catalogId", catalogId));
					param.add(new BasicNameValuePair("LeafID", articalId));
					HttpPost request = null;
					request = new HttpPost(MConfig.mIncreaseHitCountUrl);
					HttpEntity entity = new UrlEncodedFormEntity(param, "UTF-8");
					Log.i("dzy", MConfig.mIncreaseHitCountUrl + param.toString());
					request.setEntity(entity);
					BasicHttpParams httpParams = new BasicHttpParams();
					HttpConnectionParams.setConnectionTimeout(httpParams, MConfig.TIMEOUT_DEFAULT);
					HttpConnectionParams.setSoTimeout(httpParams, MConfig.TIMEOUT_DEFAULT);
					HttpResponse httpResponse = new DefaultHttpClient(httpParams).execute(request);
					if (httpResponse == null) {
						throw new Exception("HttpResponse is null.");
					}
					if (httpResponse.getStatusLine().getStatusCode() != 200) {
						throw new Exception(httpResponse.getStatusLine().getReasonPhrase());
					}
				} catch (Exception e) {
				}
				return null;
			}
		}.execute();
	}

	public static JSONArray versionUpdate(Context ctx, OnJsonArrayResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "versionUpdate"));
			param.add(new BasicNameValuePair("siteId", String.valueOf(MConfig.SITE_ID)));
			if (listener == null) {
				return HttpInvoke.invokePostJsonArray(param);
			} else {
				HttpInvoke.asyncInvokePostJsonArray(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONArray getCollect(String username, String type, Context ctx, OnJsonArrayResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "getCollect"));
			param.add(new BasicNameValuePair("siteId", String.valueOf(MConfig.SITE_ID)));
			param.add(new BasicNameValuePair("type", type));
			param.add(new BasicNameValuePair("username", username));
			if (listener == null) {
				return HttpInvoke.invokePostJsonArray(param);
			} else {
				HttpInvoke.asyncInvokePostJsonArray(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONArray addCollect(String username, String articleId, Context ctx,
			OnJsonArrayResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "addCollect"));
			param.add(new BasicNameValuePair("siteId", String.valueOf(MConfig.SITE_ID)));
			param.add(new BasicNameValuePair("articleId", articleId));
			param.add(new BasicNameValuePair("username", username));
			if (listener == null) {
				return HttpInvoke.invokePostJsonArray(param);
			} else {
				HttpInvoke.asyncInvokePostJsonArray(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONArray deleteCollect(String username, String articleId, Context ctx,
			OnJsonArrayResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "deleteCollect"));
			param.add(new BasicNameValuePair("siteId", String.valueOf(MConfig.SITE_ID)));
			param.add(new BasicNameValuePair("articleId", articleId));
			param.add(new BasicNameValuePair("username", username));
			if (listener == null) {
				return HttpInvoke.invokePostJsonArray(param);
			} else {
				HttpInvoke.asyncInvokePostJsonArray(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONArray getBrokeCatalogList(String typeId, int getAllData, Context ctx,
			OnJsonArrayResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "getCatalogList"));
			param.add(new BasicNameValuePair("siteId", String.valueOf(MConfig.SITE_ID)));
			if (typeId != null) {
				param.add(new BasicNameValuePair("type", typeId));
			}
			param.add(new BasicNameValuePair("getAllData", String.valueOf(getAllData)));
			if (listener == null) {
				return HttpInvoke.invokePostJsonArray(param);
			} else {
				HttpInvoke.asyncInvokePostJsonArray(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONObject getBrokeArticleList(String catalogId, String username, int status, int limit, int index,
			Context ctx, OnJsonObjectResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "getRevelationsList"));
			param.add(new BasicNameValuePair("siteId", String.valueOf(MConfig.SITE_ID)));
			if (catalogId != null) {
				param.add(new BasicNameValuePair("catalogId", catalogId));
			}
			if (username != null) {
				param.add(new BasicNameValuePair("username", username));
			}
			param.add(new BasicNameValuePair("status", String.valueOf(status)));
			param.add(new BasicNameValuePair("pageNum", String.valueOf(index)));
			param.add(new BasicNameValuePair("pageSize", String.valueOf(limit)));
			param.add(new BasicNameValuePair("sortField", "PublishDate"));
			param.add(new BasicNameValuePair("sort", "DESC"));
			if (listener == null) {
				return HttpInvoke.invokePostJsonObject(param);
			} else {
				HttpInvoke.asyncInvokePostJsonObject(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONArray getBrokeArticleById(String revelationId, int status, Context ctx,
			OnJsonArrayResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "getRevelationsById"));
			param.add(new BasicNameValuePair("siteId", String.valueOf(MConfig.SITE_ID)));
			if (revelationId != null) {
				param.add(new BasicNameValuePair("rid", revelationId));
			}
			param.add(new BasicNameValuePair("status", String.valueOf(status)));
			if (listener == null) {
				return HttpInvoke.invokePostJsonArray(param);
			} else {
				HttpInvoke.asyncInvokePostJsonArray(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONArray addBrokes(String username, String catalogId, String phone, String email, String address,
			String title, String content, Context ctx, OnJsonArrayResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "addRevelations"));
			param.add(new BasicNameValuePair("siteId", String.valueOf(MConfig.SITE_ID)));
			param.add(new BasicNameValuePair("cid", catalogId));
			param.add(new BasicNameValuePair("name", username));
			param.add(new BasicNameValuePair("phone", phone));
			param.add(new BasicNameValuePair("email", email));
			param.add(new BasicNameValuePair("address", address));
			param.add(new BasicNameValuePair("title", title));
			param.add(new BasicNameValuePair("content", content));
			if (listener == null) {
				return HttpInvoke.invokePostJsonArray(param);
			} else {
				HttpInvoke.asyncInvokePostJsonArray(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONArray pushNotifiToAndroid(String lastTime, Context ctx, OnJsonArrayResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "pushNotifiToAndroid"));
			param.add(new BasicNameValuePair("SiteID", String.valueOf(MConfig.SITE_ID)));
			if (lastTime != null) {
				param.add(new BasicNameValuePair("Time", lastTime));
			}
			if (listener == null) {
				return HttpInvoke.invokePostJsonArray(param);
			} else {
				HttpInvoke.asyncInvokePostJsonArray(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONArray getMobileCaptcha(String phone, Context ctx, OnJsonArrayResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "getMobileCaptcha"));
			param.add(new BasicNameValuePair("mobile", phone));
			if (listener == null) {
				return HttpInvoke.invokePostJsonArray(param);
			} else {
				HttpInvoke.asyncInvokePostJsonArray(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONArray getMobileCaptchaTwo(String phone, String nickname, Context ctx,
			OnJsonArrayResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "getMobileCaptchaTwo"));
			param.add(new BasicNameValuePair("mobile", phone));
			param.add(new BasicNameValuePair("nickname", nickname));
			param.add(new BasicNameValuePair("siteId", "" + MConfig.SITE_ID));
			if (listener == null) {
				return HttpInvoke.invokePostJsonArray(param);
			} else {
				HttpInvoke.asyncInvokePostJsonArray(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	/**
	 * 通过手机号注册
	 * 
	 * @param username
	 * @param password
	 * @param captcha
	 * @param ctx
	 * @param listener
	 * @return
	 */
	public static JSONArray mobileRegister(String username, String password, String captcha, Context ctx,
			OnJsonArrayResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "mobileRegister"));
			param.add(new BasicNameValuePair("siteId", String.valueOf(MConfig.SITE_ID)));
			param.add(new BasicNameValuePair("userName", username));
			param.add(new BasicNameValuePair("Password", password));
			param.add(new BasicNameValuePair("captcha", captcha));
			if (listener == null) {
				return HttpInvoke.invokePostJsonArray(param);
			} else {
				HttpInvoke.asyncInvokePostJsonArray(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONArray pushHomePageRec(String time, Context ctx, OnJsonArrayResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "pushHomePageRec"));
			param.add(new BasicNameValuePair("SiteID", String.valueOf(MConfig.SITE_ID)));
			if (!TextUtils.isEmpty(time)) {
				param.add(new BasicNameValuePair("Time", time));
			}
			if (listener == null) {
				return HttpInvoke.invokePostJsonArray(param);
			} else {
				HttpInvoke.asyncInvokePostJsonArray(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONObject getSpecialArticleList(String catalogId, int limit, int index, Context ctx,
			OnJsonObjectResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "getSpecialArticleList"));
			param.add(new BasicNameValuePair("siteId", String.valueOf(MConfig.SITE_ID)));
			if (catalogId != null) {
				param.add(new BasicNameValuePair("catalogId", catalogId));
			}
			param.add(new BasicNameValuePair("pageNum", String.valueOf(index)));
			param.add(new BasicNameValuePair("pageSize", String.valueOf(limit)));
			if (listener == null) {
				return HttpInvoke.invokePostJsonObject(param);
			} else {
				HttpInvoke.asyncInvokePostJsonObject(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONArray getSpecialArticleDetail(String catalogId, Context ctx, OnJsonArrayResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "getSpecialArticleDetail"));
			param.add(new BasicNameValuePair("siteId", String.valueOf(MConfig.SITE_ID)));
			param.add(new BasicNameValuePair("catalogId", catalogId));
			if (listener == null) {
				return HttpInvoke.invokePostJsonArray(param);
			} else {
				HttpInvoke.asyncInvokePostJsonArray(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONArray getCouponList(Context ctx, OnJsonArrayResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "getCouponList"));
			param.add(new BasicNameValuePair("SiteID", String.valueOf(MConfig.SITE_ID)));
			if (listener == null) {
				return HttpInvoke.invokePostJsonArray(param);
			} else {
				HttpInvoke.asyncInvokePostJsonArray(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONObject sendCouponSms(String tel, String couponId, String userName, Context ctx,
			OnJsonObjectResultListener listener) {
		try {
			if (TextUtils.isEmpty(tel) || TextUtils.isEmpty(couponId) || TextUtils.isEmpty(userName)) {
				return null;
			}
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "sendCouponSms"));
			param.add(new BasicNameValuePair("Tel", tel));
			param.add(new BasicNameValuePair("CouponID", couponId));
			param.add(new BasicNameValuePair("UserID", userName));
			if (listener == null) {
				return HttpInvoke.invokePostJsonObject(param);
			} else {
				HttpInvoke.asyncInvokePostJsonObject(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONArray getAdvertisement(Context ctx, String catalogId, String type,
			OnJsonArrayResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "getAdvertisement"));
			param.add(new BasicNameValuePair("siteId", String.valueOf(MConfig.SITE_ID)));
			if (!TextUtils.isEmpty(catalogId)) {
				param.add(new BasicNameValuePair("catalogId", catalogId));
			}
			param.add(new BasicNameValuePair("type", type));
			if (listener == null) {
				return HttpInvoke.invokePostJsonArray(param);
			} else {
				HttpInvoke.asyncInvokePostJsonArray(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONObject getVideoAd(JSONObject parameter, Context ctx,
			HttpInvokeIndividualUrl.OnJsonObjectResultListener listener) {
		try {
			if (parameter == null) {
				return null;
			}
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("siteid", MConfig.mVideoAdSiteId));
			param.add(new BasicNameValuePair("parameter", parameter.toString()));
			HttpInvokeIndividualUrl.asyncInvokePostJsonObject(MConfig.mVideoAdUrl, param, ctx, listener);
			return null;
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONObject getArticleRelaVote(String articleId, Context ctx, OnJsonObjectResultListener listener) {
		try {
			if (TextUtils.isEmpty(articleId)) {
				return null;
			}
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "getArticleRelaVote"));
			param.add(new BasicNameValuePair("ArticleID", articleId));
			if (listener == null) {
				return HttpInvoke.invokePostJsonObject(param);
			} else {
				HttpInvoke.asyncInvokePostJsonObject(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONObject getCatalogRelaVote(String catalogId, Context ctx, OnJsonObjectResultListener listener) {
		try {
			if (TextUtils.isEmpty(catalogId)) {
				return null;
			}
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "getCatalogRelaVote"));
			param.add(new BasicNameValuePair("CatalogID", catalogId));
			if (listener == null) {
				return HttpInvoke.invokePostJsonObject(param);
			} else {
				HttpInvoke.asyncInvokePostJsonObject(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONArray getVoteItems(Context ctx, String voteID, OnJsonArrayResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "getVoteItems"));
			param.add(new BasicNameValuePair("VoteID", voteID));
			if (listener == null) {
				return HttpInvoke.invokePostJsonArray(param);
			} else {
				HttpInvoke.asyncInvokePostJsonArray(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONObject submitVote(String parameters, Context ctx, OnJsonObjectResultListener listener) {
		try {
			if (TextUtils.isEmpty(parameters)) {
				return null;
			}
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "vote"));
			param.add(new BasicNameValuePair("Parameters", parameters));
			if (listener == null) {
				return HttpInvoke.invokePostJsonObject(param);
			} else {
				HttpInvoke.asyncInvokePostJsonObject(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONArray getActivityList(Context ctx, OnJsonArrayResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "getActivityList"));
			param.add(new BasicNameValuePair("SiteID", String.valueOf(MConfig.SITE_ID)));
			if (listener == null) {
				return HttpInvoke.invokePostJsonArray(param);
			} else {
				HttpInvoke.asyncInvokePostJsonArray(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONArray getActivityList(Context ctx, String userName, OnJsonArrayResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "getActivityList"));
			param.add(new BasicNameValuePair("userName", userName));
			param.add(new BasicNameValuePair("SiteID", String.valueOf(MConfig.SITE_ID)));
			if (listener == null) {
				return HttpInvoke.invokePostJsonArray(param);
			} else {
				HttpInvoke.asyncInvokePostJsonArray(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	/**
	 * 取用户参加了的活动
	 * 
	 * @param userName
	 * @param ctx
	 * @param listener
	 * @return
	 */
	public static JSONArray getUserActivityList(String userName, Context ctx, OnJsonArrayResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "getActivityList"));
			param.add(new BasicNameValuePair("userName", userName));
			param.add(new BasicNameValuePair("SiteID", String.valueOf(MConfig.SITE_ID)));
			if (listener == null) {
				return HttpInvoke.invokePostJsonArray(param);
			} else {
				HttpInvoke.asyncInvokePostJsonArray(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONArray getActivityPlayerList(String activityId, Context ctx, OnJsonArrayResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "getActivityPlayerList"));
			param.add(new BasicNameValuePair("SiteID", String.valueOf(MConfig.SITE_ID)));
			param.add(new BasicNameValuePair("activityId", activityId));
			if (listener == null) {
				return HttpInvoke.invokePostJsonArray(param);
			} else {
				HttpInvoke.asyncInvokePostJsonArray(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONArray getActivityPlayerArticleList(String playerId, Context ctx,
			OnJsonArrayResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "getActivityPlayerArticleList"));
			param.add(new BasicNameValuePair("SiteID", String.valueOf(MConfig.SITE_ID)));
			param.add(new BasicNameValuePair("playerId", playerId));
			if (listener == null) {
				return HttpInvoke.invokePostJsonArray(param);
			} else {
				HttpInvoke.asyncInvokePostJsonArray(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONArray getActivityPlayerArticleDetail(String playerId, String articleId, Context ctx,
			OnJsonArrayResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "getActivityPlayerArticleDetail"));
			param.add(new BasicNameValuePair("SiteID", String.valueOf(MConfig.SITE_ID)));
			param.add(new BasicNameValuePair("playerId", playerId));
			param.add(new BasicNameValuePair("articleId", articleId));
			if (listener == null) {
				return HttpInvoke.invokePostJsonArray(param);
			} else {
				HttpInvoke.asyncInvokePostJsonArray(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONObject voteActivityPlayer(String articleId, String playerId, String playerName, String userName,
			Context ctx, OnJsonObjectResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "voteActivityPlayer"));
			param.add(new BasicNameValuePair("SiteID", String.valueOf(MConfig.SITE_ID)));
			param.add(new BasicNameValuePair("ActivityID", articleId));
			param.add(new BasicNameValuePair("PlayerID", playerId));
			param.add(new BasicNameValuePair("PlayerName", playerName));
			param.add(new BasicNameValuePair("UserName", userName));
			if (listener == null) {
				return HttpInvoke.invokePostJsonObject(param);
			} else {
				HttpInvoke.asyncInvokePostJsonObject(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONArray getActivityCatalog(String activityId, Context ctx, OnJsonArrayResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "getActivityCatalog"));
			param.add(new BasicNameValuePair("siteId", String.valueOf(MConfig.SITE_ID)));
			param.add(new BasicNameValuePair("ActivityID", activityId));
			if (listener == null) {
				return HttpInvoke.invokePostJsonArray(param);
			} else {
				HttpInvoke.asyncInvokePostJsonArray(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONObject getArticleList(String catalogId, int pageNum, int pageSize, Context ctx,
			OnJsonObjectResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "getArticleList"));
			param.add(new BasicNameValuePair("siteId", String.valueOf(MConfig.SITE_ID)));
			param.add(new BasicNameValuePair("catalogId", catalogId));
			param.add(new BasicNameValuePair("pageNum", String.valueOf(pageNum)));
			param.add(new BasicNameValuePair("pageSize", String.valueOf(pageSize)));
			param.add(new BasicNameValuePair("isActivity", "1"));
			if (listener == null) {
				return HttpInvoke.invokePostJsonObject(param);
			} else {
				HttpInvoke.asyncInvokePostJsonObject(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONArray getCommentList(String activityId, String page, String commentObjID, String type,
			Context ctx, OnJsonArrayResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "getCommentList"));
			param.add(new BasicNameValuePair("SiteID", String.valueOf(MConfig.SITE_ID)));
			param.add(new BasicNameValuePair("activityID", activityId));
			param.add(new BasicNameValuePair("page", page));
			param.add(new BasicNameValuePair("commentObjID", commentObjID));
			param.add(new BasicNameValuePair("type", type));
			if (listener == null) {
				return HttpInvoke.invokePostJsonArray(param);
			} else {
				HttpInvoke.asyncInvokePostJsonArray(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONArray saveActivityComment(String activityId, String userName, String comment, String commentObjID,
			String type, Context ctx, OnJsonArrayResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "saveActivityComment"));
			param.add(new BasicNameValuePair("SiteID", String.valueOf(MConfig.SITE_ID)));
			param.add(new BasicNameValuePair("userName", userName));
			param.add(new BasicNameValuePair("activityID", activityId));
			param.add(new BasicNameValuePair("commentObjID", commentObjID));
			param.add(new BasicNameValuePair("comment", comment));
			param.add(new BasicNameValuePair("type", type));
			if (listener == null) {
				return HttpInvoke.invokePostJsonArray(param);
			} else {
				HttpInvoke.asyncInvokePostJsonArray(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONObject getActivitySignUpPage(String ActivityID, Context ctx,
			OnJsonObjectResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "getActivitySignUpPage"));
			param.add(new BasicNameValuePair("siteId", String.valueOf(MConfig.SITE_ID)));
			param.add(new BasicNameValuePair("ActivityID", ActivityID));
			if (listener == null) {
				return HttpInvoke.invokePostJsonObject(param);
			} else {
				HttpInvoke.asyncInvokePostJsonObject(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONObject getActivityDetails(String ActivityID, String UserName, Context ctx,
			OnJsonObjectResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "getActivityDetails"));
			param.add(new BasicNameValuePair("SiteID", String.valueOf(MConfig.SITE_ID)));
			param.add(new BasicNameValuePair("ActivityID", ActivityID));
			param.add(new BasicNameValuePair("UserName", UserName));
			if (listener == null) {
				return HttpInvoke.invokePostJsonObject(param);
			} else {
				HttpInvoke.asyncInvokePostJsonObject(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONArray getActivityAlbums(String ActivityID, Context ctx, OnJsonArrayResultListener listener) {
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("method", "getActivityAlbums"));
			param.add(new BasicNameValuePair("SiteID", String.valueOf(MConfig.SITE_ID)));
			param.add(new BasicNameValuePair("ActivityID", ActivityID));
			if (listener == null) {
				return HttpInvoke.invokePostJsonArray(param);
			} else {
				HttpInvoke.asyncInvokePostJsonArray(param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	/*----------------圈子相关接口-------------------------*/
	/**
	 * 获取圈子列表
	 * 
	 * @param uid
	 * @param ctx
	 * @param listener
	 * @return
	 */
	public static JSONObject getGroupList(String uid, Context ctx, OnJsonObjectResultListener listener) {
		try {
			StringBuilder builder = new StringBuilder();
			builder.append(MConfig.mQuanZiApiUrl).append("&").append("action").append("=").append("getGroupList")
					.append("&").append("siteid").append("=").append(MConfig.SITE_ID_QUANZI).append("&").append("uid")
					.append("=").append(uid);

			String url = builder.toString();

			if (listener == null) {
				return HttpInvoke.invokeGetJsonObject(url);
			} else {
				HttpInvoke.asyncInvokeGetJsonObject(url, ctx, listener);
				return null;
			}

		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	/**
	 * 关注圈子
	 * http://qz.ieator.com/plugin.php?id=sobeyapp:api&action=followGroup&uid
	 * =1&groupId=24&follow=1
	 * 
	 * @param uid
	 * @param follow
	 * @param groupId
	 * @param ctx
	 * @param listener
	 * @return
	 */
	public static JSONObject followGroup(String uid, int follow, String groupId, Context ctx,
			OnJsonObjectResultListener listener) {
		try {
			StringBuilder builder = new StringBuilder();
			builder.append(MConfig.mQuanZiApiUrl).append("&").append("action").append("=").append("followGroup")
					.append("&").append("uid").append("=").append(uid).append("&").append("groupId").append("=")
					.append(groupId).append("&").append("follow").append("=").append(follow);

			String url = builder.toString();

			if (listener == null) {
				return HttpInvoke.invokeGetJsonObject(url);
			} else {
				HttpInvoke.asyncInvokeGetJsonObject(url, ctx, listener);
				return null;
			}

		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	/**
	 * 收藏主题
	 * 
	 * @param uid
	 * @param subjectId
	 * @param isCollect
	 * @param ctx
	 * @param listener
	 * @return
	 */
	public static JSONObject collectSubject(String uid, String subjectId, int isCollect, Context ctx,
			OnJsonObjectResultListener listener) {
		try {
			StringBuilder builder = new StringBuilder();
			builder.append(MConfig.mQuanZiApiUrl).append("&").append("action").append("=").append("collectSubject")
					.append("&").append("uid").append("=").append(uid).append("&").append("subjectId").append("=")
					.append(subjectId).append("&").append("isCollect").append("=").append(isCollect);

			String url = builder.toString();

			if (listener == null) {
				return HttpInvoke.invokeGetJsonObject(url);
			} else {
				HttpInvoke.asyncInvokeGetJsonObject(url, ctx, listener);
				return null;
			}

		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	/**
	 * 
	 * @param method
	 *            请求方法 searchGroup/searchUser/searSubject
	 *            http://qz.ieator.com/plugin
	 *            .php?id=sobeyapp:api&action=searchGroup&siteid=1&keyWord=123
	 * @param uid
	 * @param keyWord
	 * @param ctx
	 * @param listener
	 * @return
	 */
	public static JSONObject search(String searAction, String keyWord, int page, Context ctx,
			OnJsonObjectResultListener listener) {
		try {
			StringBuilder builder = new StringBuilder();
			builder.append(MConfig.mQuanZiApiUrl).append("&").append("action").append("=").append(searAction)
					.append("&").append("siteid").append("=").append(MConfig.SITE_ID_QUANZI).append("&").append("page")
					.append("=").append(1).append("&").append("keyWord").append("=").append(keyWord);

			String url = builder.toString();

			if (listener == null) {
				return HttpInvoke.invokeGetJsonObject(url);
			} else {
				HttpInvoke.asyncInvokeGetJsonObject(url, ctx, listener);
				return null;
			}

		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	/**
	 * 获取圈子用户信息
	 * 
	 * @param uid
	 * @param keyWord
	 * @param ctx
	 * @param listener
	 * @return
	 */
	public static JSONObject getGroupUserInfo(String uid, String cid, Context ctx,
			OnJsonObjectResultListener listener) {
		try {
			StringBuilder builder = new StringBuilder();
			builder.append(MConfig.mQuanZiApiUrl).append("&").append("action").append("=").append("getUserInfo")
					.append("&").append("uid").append("=").append(uid).append("&").append("cid").append("=")
					.append(cid);

			String url = builder.toString();

			if (listener == null) {
				return HttpInvoke.invokeGetJsonObject(url);
			} else {
				HttpInvoke.asyncInvokeGetJsonObject(url, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	/**
	 * 获取圈子用户帖子信息
	 * 
	 * @param uid
	 * @param keyWord
	 * @param ctx
	 * @param listener
	 * @return
	 */
	public static JSONObject getGroupUserTiezi(String uid, String cid, Context ctx,
			OnJsonObjectResultListener listener) {
		try {
			StringBuilder builder = new StringBuilder();
			builder.append(MConfig.myTieziApiUrl).append("&").append("action").append("=").append("getUserInfo")
					.append("&").append("uid").append("=").append(uid).append("&").append("cid").append("=")
					.append(cid);

			String url = builder.toString();

			if (listener == null) {
				return HttpInvoke.invokeGetJsonObject(url);
			} else {
				HttpInvoke.asyncInvokeGetJsonObject(url, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	/**
	 * 获取圈子详情
	 * 
	 * @param uid
	 * @param page
	 *            页数 目前每页默认返回20条记录
	 * @param ctx
	 * @param listener
	 * @return
	 */
	public static JSONObject getGroupInfo(String uid, String groupId, int page, String sort, String filter, Context ctx,
			OnJsonObjectResultListener listener) {
		try {
			StringBuilder builder = new StringBuilder();
			builder.append(MConfig.mQuanZiApiUrl).append("&").append("action").append("=").append("getGroupInfo")
					.append("&").append("uid").append("=").append(uid).append("&").append("groupId").append("=")
					.append(groupId).append("&").append("sort").append("=").append(sort).append("&").append("page")
					.append("=").append(String.valueOf(page));

			if (!TextUtils.isEmpty(filter)) {
				builder.append("&").append("filter").append("=").append(filter);
			}

			String url = builder.toString();

			if (listener == null) {
				return HttpInvoke.invokeGetJsonObject(url);
			} else {
				HttpInvoke.asyncInvokeGetJsonObject(url, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	/***
	 * 获取圈子主题详情
	 * 
	 * @param subjectId
	 *            http://qz.ieator.com/plugin.php?id=sobeyapp:api&action=
	 *            getSubjectInfo&subjectId=1
	 * @param ctx
	 * @param listener
	 * @return
	 */
	public static JSONObject getGroupSubjectInfo(String uid, String subjectId, int page, String filter, Context ctx,
			OnJsonObjectResultListener listener) {
		try {
			StringBuilder builder = new StringBuilder();
			builder.append(MConfig.mQuanZiApiUrl).append("&").append("action").append("=").append("getSubjectInfo")
					.append("&").append("subjectId").append("=").append(subjectId).append("&").append("page")
					.append("=").append(page).append("&").append("uid").append("=").append(uid);
			if (!TextUtils.isEmpty(filter)) {
				builder.append("&").append("filter").append("=").append(filter);
			}
			String url = builder.toString();

			if (listener == null) {
				return HttpInvoke.invokeGetJsonObject(url);
			} else {
				HttpInvoke.asyncInvokeGetJsonObject(url, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	/**
	 * 主题点赞接口
	 * 
	 * @param subjectId
	 * @param uid
	 * @param like
	 * @param ctx
	 * @param listener
	 * @return
	 */
	public static JSONObject likeSubject(String uid, String subjectId, int like, Context ctx,
			OnJsonObjectResultListener listener) {
		try {
			StringBuilder builder = new StringBuilder();
			builder.append(MConfig.mQuanZiApiUrl).append("&").append("action").append("=").append("likeSubject")
					.append("&").append("subjectId").append("=").append(subjectId).append("&").append("like")
					.append("=").append(like).append("&").append("uid").append("=").append(uid);

			String url = builder.toString();

			if (listener == null) {
				return HttpInvoke.invokeGetJsonObject(url);
			} else {
				HttpInvoke.asyncInvokeGetJsonObject(url, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONObject likeComment(String uid, String commentId, int like, Context ctx,
			OnJsonObjectResultListener listener) {
		try {
			StringBuilder builder = new StringBuilder();
			builder.append(MConfig.mQuanZiApiUrl).append("&").append("action").append("=").append("likeComment")
					.append("&").append("commentId").append("=").append(commentId).append("&").append("like")
					.append("=").append(like).append("&").append("uid").append("=").append(uid);

			String url = builder.toString();

			if (listener == null) {
				return HttpInvoke.invokeGetJsonObject(url);
			} else {
				HttpInvoke.asyncInvokeGetJsonObject(url, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	/**
	 * 发布主题（涉及文件上传）
	 * 
	 * @param subjectId
	 * @param like
	 * @param ctx
	 * @param listener
	 * @return
	 */
	public static JSONObject postSubject(String uid, String groupId, String subjectTitle, String subjectContent,
			String subjectPics, String linkFriends, String locationInfo, Context ctx,
			OnJsonObjectResultListener listener) {
		try {
			String url = MConfig.mQuanZiApiUrl;
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("action", "postSubject"));
			param.add(new BasicNameValuePair("uid", uid));
			param.add(new BasicNameValuePair("groupId", groupId));
			param.add(new BasicNameValuePair("subjectTitle", subjectTitle));
			param.add(new BasicNameValuePair("subjectContent", subjectContent));
			// param.add(new BasicNameValuePair("subjectPics", subjectPics));
			// param.add(new BasicNameValuePair("linkFriends", linkFriends));
			// param.add(new BasicNameValuePair("locationInfo", locationInfo));
			if (listener == null) {
				return HttpInvoke.invokePostJsonObject(url, param);
			} else {
				HttpInvoke.asyncInvokePostJsonObject(url, param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	/**
	 * 发布评论（涉及文件上传）
	 * 
	 * @param uid
	 * @param subjectId
	 * @param comment
	 * @param ctx
	 * @param commentPics
	 * @param listener
	 * @return
	 */
	public static JSONObject postComment(String uid, String subjectId, String linkFrienduid, String commentContent,
			String commentPics, Context ctx, OnJsonObjectResultListener listener) {
		try {
			String url = MConfig.mQuanZiApiUrl;
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("action", "postComment"));
			param.add(new BasicNameValuePair("uid", uid));
			param.add(new BasicNameValuePair("subjectId", subjectId));
			param.add(new BasicNameValuePair("commentContent", commentContent));
			param.add(new BasicNameValuePair("linkFrienduid", linkFrienduid));
			// param.add(new BasicNameValuePair("commentPics", commentPics));
			// param.add(new BasicNameValuePair("linkFriends", linkFriends));
			// param.add(new BasicNameValuePair("locationInfo", locationInfo));
			if (listener == null) {
				return HttpInvoke.invokePostJsonObject(url, param);
			} else {
				HttpInvoke.asyncInvokePostJsonObject(url, param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONObject postReply(String subjectId, String commentId, String replyUserId, String replyContent,
			String replyPics, String linkFriends, String locationInfo, Context ctx,
			OnJsonObjectResultListener listener) {

		try {
			String url = MConfig.mQuanZiApiUrl;
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("action", "postReply"));
			param.add(new BasicNameValuePair("subjectId", subjectId));
			param.add(new BasicNameValuePair("commentId", commentId));
			param.add(new BasicNameValuePair("replyUserId", replyUserId));
			param.add(new BasicNameValuePair("replyContent", replyContent));
			// param.add(new BasicNameValuePair("replyPics", replyPics));
			// param.add(new BasicNameValuePair("linkFriends", linkFriends));
			// param.add(new BasicNameValuePair("locationInfo", locationInfo));
			if (listener == null) {
				return HttpInvoke.invokePostJsonObject(url, param);
			} else {
				HttpInvoke.asyncInvokePostJsonObject(url, param, ctx, listener);
				return null;
			}
		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	/**
	 * 从cms中取资讯配置项返回的id
	 * 
	 * @return
	 */
	public static JSONObject getNewsCatalogIDs() {
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("method", "appConfig"));
		String appkey = MConfig.ZiXun_AppKey;
		if (!appkey.contains("21")) {
			appkey = appkey.replace("_chengyang", "");
		}
		param.add(new BasicNameValuePair("appkey", appkey));
		return HttpInvoke.invokePostJsonObject(MConfig.mServerUrl, param);
	}

	/**
	 * 从cms中取置顶新闻
	 * 
	 * @param siteId
	 *            站点id
	 * @param pageIndex
	 *            页数
	 * @param pageCount
	 *            一页取多少条
	 * @param listener
	 * @return
	 */
	public static JSONObject getTopNewsFromCMS(int siteId, int pageIndex, int pageCount, Context ctx,
			OnJsonObjectResultListener listener) {
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("method", "getSiteTop"));
		param.add(new BasicNameValuePair("siteId", String.valueOf(MConfig.SITE_ID)));
		param.add(new BasicNameValuePair("TerminalType", MConfig.TerminalType));
		param.add(new BasicNameValuePair("pageNum", String.valueOf(pageIndex)));
		param.add(new BasicNameValuePair("pageSize", String.valueOf(pageCount)));
		param.add(new BasicNameValuePair("isTop", String.valueOf(1)));
		param.add(new BasicNameValuePair("showcatalog", String.valueOf(1)));// 新加的接口参数
																			// 用于返回栏目名称和栏目的图标用的
		param.add(new BasicNameValuePair("sortField", "PUBLISHDATE"));
		param.add(new BasicNameValuePair("sort", "DESC"));
		if (listener == null)
			return HttpInvoke.invokePostJsonObject(MConfig.mServerUrl, param);
		HttpInvoke.asyncInvokePostJsonObject(MConfig.mServerUrl, param, ctx, listener);
		return null;
	}

	/**
	 * 取普通 新闻 从cms
	 * 
	 * @param siteId
	 * @param pageIndex
	 * @param pageCount
	 * @param ctx
	 * @param listener
	 * @return
	 */
	public static JSONObject getNormaNewsFromCMS(int siteId, int pageIndex, int pageCount, Context ctx,
			OnJsonObjectResultListener listener) {
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("method", "getSiteTop"));
		param.add(new BasicNameValuePair("siteId", "" + MConfig.SITE_ID));
		param.add(new BasicNameValuePair("TerminalType", MConfig.TerminalType));
		param.add(new BasicNameValuePair("pageNum", String.valueOf(pageIndex)));
		param.add(new BasicNameValuePair("pageSize", String.valueOf(pageCount)));
		param.add(new BasicNameValuePair("isTop", "0"));
		param.add(new BasicNameValuePair("showcatalog", String.valueOf(1)));// 新加的接口参数
																			// 用于返回栏目名称和栏目的图标用的
		param.add(new BasicNameValuePair("sortField", "PUBLISHDATE"));
		param.add(new BasicNameValuePair("sort", "DESC"));
		if (listener == null)
			return HttpInvoke.invokePostJsonObject(MConfig.mServerUrl, param);
		HttpInvoke.asyncInvokePostJsonObject(MConfig.mServerUrl, param, ctx, listener);
		return null;
	}

	/**
	 * 我的收藏主题
	 * 
	 * @param uid
	 * @param follow
	 * @param groupId
	 * @param ctx
	 * @param listener
	 * @return
	 */
	public static JSONObject getcollectedSubjectList(String uid, Context ctx, OnJsonObjectResultListener listener) {
		try {
			StringBuilder builder = new StringBuilder();
			builder.append(MConfig.mQuanZiApiUrl).append("&").append("action").append("=")
					.append("getcollectedSubjectList").append("&").append("uid").append("=").append(uid);
			String url = builder.toString();
			if (listener == null) {
				return HttpInvoke.invokeGetJsonObject(url);
			} else {
				HttpInvoke.asyncInvokeGetJsonObject(url, ctx, listener);
				return null;
			}

		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONObject getUserMessageList(String uid, int page, Context ctx,
			OnJsonObjectResultListener listener) {
		try {
			StringBuilder builder = new StringBuilder();
			builder.append(MConfig.mQuanZiApiUrl).append("&").append("action").append("=").append("getUserMessageList")
					.append("&").append("uid").append("=").append(uid).append("&").append("page").append("=")
					.append(String.valueOf(page)).append("&").append("siteid").append("=")
					.append(MConfig.SITE_ID_QUANZI);
			String url = builder.toString();
			if (listener == null) {
				return HttpInvoke.invokeGetJsonObject(url);
			} else {
				HttpInvoke.asyncInvokeGetJsonObject(url, ctx, listener);
				return null;
			}

		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONObject getUserPrivateLetterList(String uid, int page, Context ctx,
			OnJsonObjectResultListener listener) {
		try {
			StringBuilder builder = new StringBuilder();
			builder.append(MConfig.mQuanZiApiUrl).append("&").append("action").append("=")
					.append("getUserPrivateLetterList").append("&").append("uid").append("=").append(uid).append("&")
					.append("page").append("=").append(String.valueOf(page)).append("&").append("siteid").append("=")
					.append(MConfig.SITE_ID_QUANZI);
			String url = builder.toString();
			if (listener == null) {
				return HttpInvoke.invokeGetJsonObject(url);
			} else {
				HttpInvoke.asyncInvokeGetJsonObject(url, ctx, listener);
				return null;
			}

		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONObject getUserPrivateLetterDetail(String uid, String letterId, int page, Context ctx,
			OnJsonObjectResultListener listener) {
		try {
			StringBuilder builder = new StringBuilder();
			builder.append(MConfig.mQuanZiApiUrl).append("&").append("action").append("=")
					.append("getUserPrivateLetterDetail").append("&").append("uid").append("=").append(uid).append("&")
					.append("letterId").append("=").append(letterId).append("&").append("page").append("=")
					.append(String.valueOf(page)).append("&").append("siteid").append("=")
					.append(MConfig.SITE_ID_QUANZI);
			String url = builder.toString();
			if (listener == null) {
				return HttpInvoke.invokeGetJsonObject(url);
			} else {
				HttpInvoke.asyncInvokeGetJsonObject(url, ctx, listener);
				return null;
			}

		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	public static JSONObject followUser(String uid, String toUid, int follow, Context ctx,
			OnJsonObjectResultListener listener) {
		try {
			StringBuilder builder = new StringBuilder();
			builder.append(MConfig.mQuanZiApiUrl).append("&").append("action").append("=").append("followUser")
					.append("&").append("uid").append("=").append(uid).append("&").append("toUid").append("=")
					.append(toUid).append("&").append("follow").append("=").append(String.valueOf(follow));
			String url = builder.toString();
			if (listener == null) {
				return HttpInvoke.invokeGetJsonObject(url);
			} else {
				HttpInvoke.asyncInvokeGetJsonObject(url, ctx, listener);
				return null;
			}

		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	/**
	 * 更新圈子用户昵称
	 * 
	 * @param uid
	 * @param niceName
	 * @param sex
	 * @param email
	 * @param ctx
	 * @param listener
	 * @return
	 */
	public static JSONObject modifyUserInfo(String uid, String niceName, String sex, String email, String telphone,
			Context ctx, OnJsonObjectResultListener listener) {
		try {
			StringBuilder builder = new StringBuilder();
			builder.append(MConfig.mQuanZiApiUrl).append("&").append("action").append("=").append("modifyUserInfo")
					.append("&").append("uid").append("=").append(uid).append("&").append("niceName").append("=")
					.append(niceName).append("&").append("email").append("=").append(email).append("&").append("sex")
					.append("=").append(sex);// .append("&Telphone="+telphone);
			String url = builder.toString();
			if (listener == null) {
				return HttpInvoke.invokeGetJsonObject(url);
			} else {
				HttpInvoke.asyncInvokeGetJsonObject(url, ctx, listener);
				return null;
			}

		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	/**
	 * 取惠州台首页关注最大的条条数
	 * 
	 * @return
	 */
	public static JSONObject getAttentionTopNewsMaxCount() {
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("method", "appConfig"));
		String appkey = MConfig.MAX_Attetion_Topic_KEY;
		if (!appkey.contains("21")) {
			appkey = appkey.replace("_chengyang", "");
		}
		param.add(new BasicNameValuePair("appkey", appkey));
		return HttpInvoke.invokePostJsonObject(MConfig.mServerUrl, param);
	}

	/**
	 * 获取电商收获地址
	 * 
	 * @param listener
	 * @return
	 */
	public static JSONObject getEbusinessMyAddress(String uid, int page, Context ctx,
			OnJsonObjectResultListener listener) {
		try {
			StringBuilder builder = new StringBuilder();
			builder.append(MConfig.ECSHOPApiUrl).append("&").append("action").append("=").append("address").append("&")
					.append("uid").append("=").append(uid).append("&").append("page").append("=")
					.append(String.valueOf(page));
			String url = builder.toString();
			if (listener == null) {
				return HttpInvoke.invokeGetJsonObject(url);
			} else {
				HttpInvoke.asyncInvokeGetJsonObject(url, ctx, listener);
				return null;
			}

		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	/**
	 * 获取电商我的收藏
	 * 
	 * @param listener
	 * @return http://shop.sobeycache.com/index.php?controller=interfaceapi&
	 *         action =favorite&uid=2&page=1&site_id=21
	 */
	public static JSONObject getEbusinessMyCollect(String uid, int page, int pagesize, Context ctx,
			OnJsonObjectResultListener listener) {

		try {
			StringBuilder builder = new StringBuilder();
			builder.append(MConfig.ECSHOPApiUrl).append("&").append("action").append("=").append("favorite").append("&")
					.append("page").append("=").append(page).append("&").append("pagesize").append("=").append(pagesize)
					.append("&").append("uid").append("=").append(uid).append("&").append("site_id").append("=")
					.append(MConfig.SITE_ID);
			String url = builder.toString();
			if (listener == null) {
				return HttpInvoke.invokeGetJsonObject(url);
			} else {
				HttpInvoke.asyncInvokeGetJsonObject(url, ctx, listener);
				return null;
			}

		} catch (Exception ex) {
			if (listener != null)
				listener.onNG(ex.toString());
			return null;
		}
	}

	/**
	 * 请求电商我的订单
	 * 
	 * @param uid
	 * @return
	 */
	public static JSONObject getEBusinessMyOrader(String uid, int pageIndex, int pageSize) {
		StringBuilder builder = new StringBuilder();
		JSONObject jsonArray = null;
		try {

			builder.append(MConfig.ECSHOP_ORDERAPI).append("&").append("action").append("=").append("order").append("&")
					.append("site_id").append("=").append(MConfig.SITE_ID).append("&").append("page").append("=")
					.append(pageIndex).append("&").append("pagesize").append("=").append(pageSize).append("&")
					.append("uid").append("=").append(uid);
			String url = builder.toString();
			jsonArray = HttpInvoke.invokeGetJsonObject(url);
		} catch (Exception e) {
		}
		return jsonArray;
	}

	/**
	 * 获取我的订单列表
	 * 
	 * @param uid
	 * @param pageIndex
	 * @param getType
	 * @return
	 */
	public static JSONObject getMyOraderList(String uid, int pageIndex, int getType) {
		StringBuilder builder = new StringBuilder();
		JSONObject jsonArray = null;
		try {
			builder.append(MConfig.MYORDER_LIST).append("&").append("uid=").append(uid).append("&getType=")
					.append(getType + "").append("&page=").append(pageIndex + "");
			String url = builder.toString();
			jsonArray = HttpInvoke.invokeGetJsonObject(url);
		} catch (Exception e) {
		}
		return jsonArray;
	}

	public static JSONObject getOrderListSize(String uid) {
		StringBuilder builder = new StringBuilder();
		JSONObject jsonArray = null;
		try {
			builder.append(MConfig.ORDER_LISTSIZE).append("&").append("uid=").append(uid);
			String url = builder.toString();
			jsonArray = HttpInvoke.invokeGetJsonObject(url);
		} catch (Exception e) {
		}
		return jsonArray;
	}

	public static JSONObject getMyOrderDetail(String uid, String orderId) {
		StringBuilder builder = new StringBuilder();
		JSONObject jsonArray = null;
		try {
			builder.append(MConfig.MY_ORDER_DETAIL).append("&").append("uid=").append(uid).append("&orderId=")
					.append(orderId);
			String url = builder.toString();
			jsonArray = HttpInvoke.invokeGetJsonObject(url);
		} catch (Exception e) {
		}
		return jsonArray;
	}

	/**
	 * 请求电商我的购物车
	 * 
	 * @param uid
	 * @return
	 */
	public static JSONObject getEBusinessMyCart(String uid, int pageIndex, int pageSize,
			String iweb_shoppingcart_cookie) {
		JSONObject jsonArray = null;
		// if(TextUtils.isEmpty(iweb_shoppingcart_cookie))
		// iweb_shoppingcart_cookie="iweb_shoppingcart=\"\"";
		// else
		// {
		// StringBuilder builder2=new StringBuilder();
		// builder2.append("iweb_shoppingcart=");
		// builder2.append(iweb_shoppingcart_cookie).append("; expires=Fri,
		// 16-Mar-2035 02:23:43 GMT; path=/");
		// iweb_shoppingcart_cookie=builder2.toString();
		// }
		StringBuilder builder = new StringBuilder();
		try {
			builder.append(MConfig.ECSHOP_CARTAPI).append("&").append("action").append("=").append("cart").append("&")
					.append("site_id").append("=").append(MConfig.SITE_ID).append("&").append("page").append("=")
					.append(pageIndex).append("&").append("pagesize").append("=").append(pageSize).append("&")
					.append("uid").append("=").append(uid);
			// .append("&").append("iweb_shoppingcart").append("=").append(iweb_shoppingcart);
			String url = builder.toString();
			HashMap<String, String> header = new HashMap<String, String>();
			header.put("Cookie", iweb_shoppingcart_cookie);
			String data = HttpInvoke.invokeGetString(url, header);
			jsonArray = new JSONObject(data);
		} catch (Exception e) {
			Log.d("zxd", e.toString());
		}
		return jsonArray;

	}
}
