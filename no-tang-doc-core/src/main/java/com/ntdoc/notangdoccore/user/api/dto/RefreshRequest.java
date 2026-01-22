package com.ntdoc.notangdoccore.user.api.dto;

public class RefreshRequest {
    private String refreshToken;
    public String getRefreshToken() {
        return refreshToken;
    }
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
