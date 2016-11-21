package com.sobey.cloud.webtv.bean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sobey.cloud.webtv.utils.MConfig;

import android.text.TextUtils;

public class RequestResultParser {
	public static GetGroupListResult parseGroupsModel(JSONObject obj) {
		GetGroupListResult groupsModel = new GetGroupListResult();

		if (null != obj) {

			JSONArray jsonArray_followedGroupList = obj.optJSONArray("followedGroupList");
			groupsModel.followedGroupList = parseListGroupModel(jsonArray_followedGroupList, 1);

			JSONArray jsonArray_otherGroupList = obj.optJSONArray("otherGroupList");
			groupsModel.otherGroupList = parseListGroupModel(jsonArray_otherGroupList, 0);
		}
		return groupsModel;
	}

	public static GetUserMessageListResult parseMsgListResult(JSONObject obj) {
		GetUserMessageListResult getUserMessageListResult = new GetUserMessageListResult();
		getUserMessageListResult.msgCount = obj.optInt("msgCount");
		getUserMessageListResult.msgList = parseListMessageModel(obj.optJSONArray("msgList"));
		return getUserMessageListResult;
	}

	public static GetPrivateLetterListResult parseLetterListResult(JSONObject obj) {
		GetPrivateLetterListResult privateLetterListResult = new GetPrivateLetterListResult();
		privateLetterListResult.letterCount = obj.optInt("letterCount");
		privateLetterListResult.letterList = parseListLetterModel(obj.optJSONArray("letterList"));
		return privateLetterListResult;
	}

	public static List<GroupModel> parseListGroupModel(JSONArray array, int isFollowed) {
		List<GroupModel> groupList = new ArrayList<GroupModel>();
		if (null != array) {
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.optJSONObject(i);
				if (null != obj) {
					GroupModel groupModel = parseGroupModel(obj);
					groupModel.isFollowed = isFollowed;
					groupList.add(groupModel);
				}
			}
		}
		return groupList;
	}

	public static List<GroupUserModel> parseListUserModel(JSONArray array) {
		List<GroupUserModel> userList = new ArrayList<GroupUserModel>();
		if (null != array) {
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.optJSONObject(i);
				if (null != obj) {
					GroupUserModel userModel = parseUserModel(obj);
					userList.add(userModel);
				}
			}
		}
		return userList;
	}

	public static List<GroupSubjectModel> parseListSubjectModel(JSONArray array) {
		List<GroupSubjectModel> subjectModels = new ArrayList<GroupSubjectModel>();
		if (null != array) {
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.optJSONObject(i);
				if (null != obj) {
					GroupSubjectModel subjectModel = parseSubjectModel(obj);
					subjectModels.add(subjectModel);
				}
			}
		}
		return subjectModels;
	}

	public static List<GroupCommentModel> parseListCommentModel(JSONArray array) {
		List<GroupCommentModel> subjectModels = new ArrayList<GroupCommentModel>();
		if (null != array) {
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.optJSONObject(i);
				if (null != obj) {
					GroupCommentModel subjectModel = parseCommentModel(obj);
					subjectModels.add(subjectModel);
				}
			}
		}
		return subjectModels;
	}

	public static List<GroupCommentReplyModel> parseListCommentReplyModel(JSONArray array) {
		List<GroupCommentReplyModel> replyModels = new ArrayList<GroupCommentReplyModel>();
		if (null != array) {
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.optJSONObject(i);
				if (null != obj) {
					GroupCommentReplyModel groupCommentReplyModel = parseCommentReplyMode(obj);
					replyModels.add(groupCommentReplyModel);
				}
			}
		}
		return replyModels;
	}

	public static List<MessageModel> parseListMessageModel(JSONArray array) {
		List<MessageModel> msgList = new ArrayList<MessageModel>();
		if (null != array) {
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.optJSONObject(i);
				if (null != obj) {
					MessageModel mesasgeModel = parseMessageModel(obj);
					msgList.add(mesasgeModel);
				}
			}
		}
		return msgList;
	}

	public static List<LetterModel> parseListLetterModel(JSONArray array) {
		List<LetterModel> letterList = new ArrayList<LetterModel>();
		if (null != array) {
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.optJSONObject(i);
				if (null != obj) {
					LetterModel letterModel = parseLetterModel(obj);
					letterList.add(letterModel);
				}
			}
		}
		return letterList;
	}

	public static GetEbReciverListResult parseListEbusinessReciverModel(JSONObject obj) {

		GetEbReciverListResult reciverListResult = new GetEbReciverListResult();
		if (null != obj) {
			int page = obj.optInt("totalPage");
			reciverListResult.totalPage = page;
			List<EbusinessReciverModel> reciverModels = new ArrayList<EbusinessReciverModel>();
			JSONArray array = obj.optJSONArray("addresses");
			if (null != array) {
				for (int i = 0; i < array.length(); i++) {
					JSONObject obj1 = array.optJSONObject(i);
					if (null != obj1) {
						EbusinessReciverModel reciverModel = parseReciverModel(obj1);
						reciverModels.add(reciverModel);
					}
				}
			}
			reciverListResult.reciverModels = reciverModels;
		}
		return reciverListResult;
	}

	public static GetEbCollectListResult parseListEbusinessGoodsModel(JSONObject obj) {
		GetEbCollectListResult collectListResult = new GetEbCollectListResult();
		if (null != obj) {
			int page = obj.optInt("totalPage");
			collectListResult.totalPage = page;
			List<EbusinessGoodsModel> goodsModels = new ArrayList<EbusinessGoodsModel>();
			JSONArray array = obj.optJSONArray("goods");
			if (null != array) {
				for (int i = 0; i < array.length(); i++) {
					JSONObject obj1 = array.optJSONObject(i);
					if (null != obj1) {
						EbusinessGoodsModel goodsModel = parseGoodsModel(obj1);
						goodsModels.add(goodsModel);
					}
				}
			}
			collectListResult.goods = goodsModels;
		}
		return collectListResult;
	}

	public static GroupModel parseGroupModel(JSONObject obj) {
		GroupModel groupModel = new GroupModel();
		groupModel.groupId = obj.optString("groupId");
		StringBuilder sb = new StringBuilder();
		if (!TextUtils.isEmpty(obj.optString("headUrl"))) {
			sb.append(MConfig.mQuanZiResourceUrl).append(obj.optString("headUrl"));
			groupModel.headUrl = sb.toString();
		}
		groupModel.groupName = obj.optString("groupName");
		groupModel.groupInfo = obj.optString("groupInfo");
		groupModel.newSubjectCount = obj.optInt("newSubjectCount");
		groupModel.isFollowed = obj.optInt("isFollowed");
		groupModel.subjectCount = obj.optString("subjectCount");
		groupModel.totalSubjectCount = obj.optString("totalSubjectCount");
		groupModel.topicSubjectList = parseListSubjectModel(obj.optJSONArray("topicSubjectList"));
		groupModel.subjectList = parseListSubjectModel(obj.optJSONArray("subjectList"));
		return groupModel;
	}

	public static GroupUserModel parseUserModel(JSONObject obj) {
		GroupUserModel userModel = new GroupUserModel();
		userModel.userName = obj.optString("userName");
		if (TextUtils.isEmpty(obj.optString("userName"))) {
			userModel.userName = obj.optString("username");
		}
		userModel.uid = obj.optString("uid");
		// userModel.userHeadUrl = obj.optString("userHeadUrl");
		if (!TextUtils.isEmpty(obj.optString("userHeadUrl"))) {
			StringBuilder sb = new StringBuilder();
			sb.append(MConfig.QZ_DOMAIN).append(obj.optString("userHeadUrl"));
			userModel.userHeadUrl = sb.toString();
		}
		userModel.isFriend = obj.optInt("isFriend");
		userModel.postedSubjectList = parseListSubjectModel(obj.optJSONArray("postedSubjectList"));
		return userModel;
	}

	public static GroupSubjectModel parseSubjectModel(JSONObject obj) {
		GroupSubjectModel subjectModel = new GroupSubjectModel();
		subjectModel.subjectId = obj.optString("subjectId");
		subjectModel.subjectContent = obj.optString("subjectContent");
		String url = obj.optString("subjectPicUrls");
		if (!TextUtils.isEmpty(url)) {
			String[] urls = url.split(",");
			String[] convertUrls = new String[urls.length];
			for (int i = 0; i < urls.length; i++) {
				convertUrls[i] = MConfig.QZ_DOMAIN + urls[i];
			}
			subjectModel.subjectPicUrls = convertUrls;
		}
		subjectModel.subjectTitle = obj.optString("subjectTitle");
		subjectModel.subjectLikeCount = obj.optString("subjectLikeCount");
		subjectModel.subjectReplyCount = obj.optString("subjectReplyCount");
		subjectModel.publishTime = obj.optString("publishTime");
		// subjectModel.likedUserCount = obj.optString("likedUserCount");
		String publishUserName = obj.optString("publishUserName");
		if (publishUserName == null || publishUserName.equals("") || publishUserName.equals("null")) {
			publishUserName = "";
		}
		subjectModel.publishUserName = publishUserName;
		subjectModel.publishUserId = obj.optString("publishUserId");
		subjectModel.groupId = obj.optString("groupId");
		subjectModel.groupName = obj.optString("groupName");
		subjectModel.groupHeadUrl = obj.optString("groupHeadUrl");
		subjectModel.groupInfo = obj.optString("groupInfo");
		subjectModel.isLiked = obj.optInt("isLiked");
		subjectModel.isCollected = obj.optInt("isCollected");
		if (!TextUtils.isEmpty(obj.optString("subjectUrl"))) {
			subjectModel.subjectUrl = MConfig.QZ_DOMAIN + obj.optString("subjectUrl");
		}

		subjectModel.commentCount = obj.optInt("commentCount");
		subjectModel.digest = obj.optInt("digest");

		if (!TextUtils.isEmpty(obj.optString("publishUserHeadUrl"))) {
			StringBuilder sb = new StringBuilder();
			if (obj.optString("publishUserHeadUrl").contains("http")) {
				subjectModel.publishUserHeadUrl = obj.optString("publishUserHeadUrl");
			} else {
				sb.append(MConfig.QZ_DOMAIN).append(obj.optString("publishUserHeadUrl"));
				subjectModel.publishUserHeadUrl = sb.toString();
			}

		}

		subjectModel.likedUserList = parseListUserModel(obj.optJSONArray("likedUserList"));
		subjectModel.commentList = parseListCommentModel(obj.optJSONArray("commentList"));
		return subjectModel;
	}

	public static GroupCommentModel parseCommentModel(JSONObject obj) {
		GroupCommentModel commentModel = new GroupCommentModel();
		commentModel.commentContent = obj.optString("commentContent");
		commentModel.commentId = obj.optString("commentId");
		// commentModel.commentUser =
		// parseUserModel(obj.optJSONObject("commentUser"));
		commentModel.commentUserId = obj.optString("commentUserId");
		commentModel.commentUserName = obj.optString("commentUserName");
		commentModel.commentLikeCount = obj.optInt("commentLikeCount");
		commentModel.commentIsLiked = obj.optInt("commentIsLiked");
		commentModel.commentReplyContent = obj.optString("commentReplyContent");
		if (!TextUtils.isEmpty(obj.optString("CommentUserHeadUrl"))) {
			String commentUserHeadUrl = obj.optString("CommentUserHeadUrl");
			StringBuilder sb = new StringBuilder();
			if (commentUserHeadUrl.startsWith("http")) {
				commentModel.CommentUserHeadUrl = commentUserHeadUrl;
			} else {
				sb.append(MConfig.QZ_DOMAIN).append("/uc_server/").append(obj.optString("CommentUserHeadUrl"));
				commentModel.CommentUserHeadUrl = sb.toString();
			}
		}
		String url = obj.optString("commentPicUrls");
		if (!TextUtils.isEmpty(url)) {
			String[] urls = url.split(",");
			String[] convertUrls = new String[urls.length];
			for (int i = 0; i < urls.length; i++) {
				convertUrls[i] = MConfig.QZ_DOMAIN + urls[i];
			}
			commentModel.commentPicUrls = convertUrls;
		}

		commentModel.commentReplyList = parseListCommentReplyModel(obj.optJSONArray("commentReplyList"));
		commentModel.commentPostedTime = obj.optString("commentPostedTime");
		return commentModel;
	}

	public static GroupCommentReplyModel parseCommentReplyMode(JSONObject obj) {
		GroupCommentReplyModel commentReplyModel = new GroupCommentReplyModel();
		commentReplyModel.replyContent = obj.optString("replyContent");
		commentReplyModel.replyTime = obj.optString("replyTime");
		commentReplyModel.replyUserId = obj.optString("replyUserId");
		commentReplyModel.replyUserName = obj.optString("replyUserName");
		return commentReplyModel;
	}

	public static MessageModel parseMessageModel(JSONObject obj) {
		MessageModel messageModel = new MessageModel();
		messageModel.msgId = obj.optString("msgId");
		messageModel.msgPublishTime = obj.optString("msgPublishTime");
		messageModel.msgContent = obj.optString("msgContent");
		messageModel.msgPublishUserHeadUrl = obj.optString("msgPublishUserHeadUrl");
		messageModel.msgPublishUserId = obj.optString("msgPublishUserId");
		messageModel.msgPublishUserName = obj.optString("msgPublishUserName");
		messageModel.msgType = obj.optInt("msgType");
		return messageModel;
	}

	public static LetterModel parseLetterModel(JSONObject obj) {
		LetterModel letterModel = new LetterModel();
		letterModel.letterContent = obj.optString("letterContent");
		letterModel.letterCount = obj.optInt("letterCount");
		letterModel.letterId = obj.optString("letterId");
		letterModel.letterPublishTime = obj.optString("letterPublishTime");
		letterModel.letterPublishUesrName = obj.optString("letterPublishUesrName");
		letterModel.letterPublishUserHeadUrl = obj.optString("letterPublishUserHeadUrl");
		letterModel.letterPublishUserId = obj.optString("letterPublishUserId");
		letterModel.letterUrl = obj.optString("letterUrl");
		return letterModel;
	}

	public static EbusinessReciverModel parseReciverModel(JSONObject obj) {
		EbusinessReciverModel reciverModel = new EbusinessReciverModel();
		reciverModel.reciver = obj.optString("reciver");
		reciverModel.phoneNo = obj.optString("phoneNo");
		reciverModel.address = obj.optString("address");
		reciverModel.postNo = obj.optString("postNo");
		return reciverModel;
	}

	public static EbusinessGoodsModel parseGoodsModel(JSONObject obj) {
		EbusinessGoodsModel goodsModel = new EbusinessGoodsModel();
		goodsModel.sellerName = obj.optString("sellerName");
		goodsModel.sellerID = obj.optString("sellerID");
		goodsModel.status = obj.optInt("status");
		goodsModel.goodsID = obj.optString("goodsID");
		goodsModel.goodsName = obj.optString("goodsName");
		goodsModel.goodsURL = obj.optString("goodsURL");
		goodsModel.goodsImageURL = obj.optString("goodsImageURL");
		goodsModel.goodsFactPrice = obj.optString("goodsFactPrice");
		goodsModel.goodsOrginalPrice = obj.optString("goodsOrginalPrice");
		return goodsModel;
	}

	public static SobeyBaseResult parseBaseResult(JSONObject result) {
		SobeyBaseResult baseResult = new SobeyBaseResult();
		if (null != result) {
			baseResult.returnCode = result.optInt("returnCode");
		}
		return baseResult;
	}

}
