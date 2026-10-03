package com.qk.utils;

/**
 * 操作当前登录用户信息（基于 ThreadLocal，线程隔离）
 *
 * <p>文档中原类名为 {@code CurrentUserHoler}（拼写笔误），此处修正为 CurrentUserHolder。
 *
 * <p>使用流程：TokenFilter 解析 JWT 后 setCurrentUser → Service 中 getCurrentUser 取用 →
 * 请求结束 removeCurrentUser（必须在 finally 中执行，避免线程池复用导致用户信息串号）。
 */
public class CurrentUserHolder {

    private static final ThreadLocal<Integer> CURRENT_USER = new ThreadLocal<>();

    public static void setCurrentUser(Integer userId) {
        CURRENT_USER.set(userId);
    }

    public static Integer getCurrentUser() {
        return CURRENT_USER.get();
    }

    public static void removeCurrentUser() {
        CURRENT_USER.remove();
    }
}
