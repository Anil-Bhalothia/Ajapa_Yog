package com.eschool.beans;

public class MessageTemplates {
private String signupEmailMessage;
private String pendingUserTryingLogin;
private String onceAdminApprove;
private String onceAdminReject;

public MessageTemplates() {
		signupEmailMessage="We've received your signup request!<br>Our Admin team is currently reviewing your information.<br> We'll notify you via Email/SMS once your registration has been confirmed or if any further action is required.<br> Meanwhile, if you have any questions or concerns, feel free to reach out to us at <a href='mailto:contact@ajapayog.org'>contact@ajapayog.org</a>.";
		pendingUserTryingLogin="Your request is still pending for approval.<br> Meanwhile, if you have any questions or concerns, feel free to reach out to us at <a href='mailto:contact@ajapayog.org'> contact@ajapayog.org </a>.";
		onceAdminApprove="Your registration is successful. Now, you can login with your credentials.";
		onceAdminReject="Your request to join Ajapa Yog has been rejected.<br>If you have any questions or concerns, feel free to reach out to us at <a href='mailto:contact@ajapayog.org'> contact@ajapayog.org </a>.";
		
	}

public String getSignupEmailMessage() {
	return signupEmailMessage;
}

public void setSignupEmailMessage(String signupEmailMessage) {
	this.signupEmailMessage = signupEmailMessage;
}

public String getPendingUserTryingLogin() {
	return pendingUserTryingLogin;
}

public void setPendingUserTryingLogin(String pendingUserTryingLogin) {
	this.pendingUserTryingLogin = pendingUserTryingLogin;
}

public String getOnceAdminApprove() {
	return onceAdminApprove;
}

public void setOnceAdminApprove(String onceAdminApprove) {
	this.onceAdminApprove = onceAdminApprove;
}

public String getOnceAdminReject() {
	return onceAdminReject;
}

public void setOnceAdminReject(String onceAdminReject) {
	this.onceAdminReject = onceAdminReject;
}




}
