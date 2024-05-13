package dev.project.orderservice.dto;


public class ApiResponse<T> {
    private T data;
    private String message;
    private boolean success;

    public ApiResponse(T data, String message, boolean success) {
        this.data = data;
        this.message = message;
        this.success = success;
    }

    // 성공 메서드
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data, "Operation successful", true);
    }

    // URL을 포함한 성공 메세지를 처리하기 위한 새로운 메서드
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(data, message, true);
    }

    // 실패 메서드
    public static <T> ApiResponse<T> failure(String message) {
        return new ApiResponse<>(null, message, false);
    }


}
