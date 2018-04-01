package com.project.client.utils;


public final class ProjectConstant {
	public enum HttpStausCode implements com.jaxrs.simplerest.utils.ConstantInterface.HttpStausCode
	{
		OK(200),
		CREATED(201),
		TEMP_REDIRECT(307),
		BAD_REQUEST(400),
		FORBIDDEN(403),
		NOT_FOUND(404),
		METHOD_NOT_ALLOWED(405),
		INTERNAL_SERVER_ERROR(500),
		KNOWN_ERROR(530);
		private int status;
		HttpStausCode(int status)
		{
			this.status = status; 
		}
		public int getStatus()
		{
			return status;
		}
	}
	public enum ResponseContentType implements com.jaxrs.simplerest.utils.ConstantInterface.ResponseContentType{
		HTML("html"),
		JSON("json"),
		ZOHO_SHEET("zohosheet"),
		SPREAD_SHEET("spreadsheet"),
		SPSS("spss"),
		CSV("csv"),
		PDF("pdf"),
		IMAGE("image"),
		ZIP("zip");
		String type;
		ResponseContentType(String contentType) {
			this.type = contentType;
		}
		private String getType() {
			return type;
		}
		public static ResponseContentType getResponseContentType(String type) {
			for(ResponseContentType each : ResponseContentType.values()) {
				if(each.getType().equals(type)) {
					return each;
				}
			}
			return null;
		}
		public String getContentType() {
			return type;
		}
	}
}
