package com.hardy.fawatir.query;

public class UserQuery {


    public static final String INSERT_USER_QUERY = "INSERT INTO Users (first_name, last_name, email, password, non_locked, enabled) VALUES (:firstName, :lastName, :email, :password, :nonLocked, :enabled)";
    public static final String COUNT_USER_EMAIL_QUERY = "SELECT COUNT(*) FROM Users WHERE email = :email";
    public static final String INSERT_ACCOUNT_VERIFICATION_URL_QUERY = "INSERT INTO AccountVerification (user_id, url) VALUES (:userId, :url)";
    public static final String SELECT_USER_BY_IMAIL_QUERY = "SELECT * FROM Users WHERE email = :email";
    public static final String DELETE_VERIFICATION_CODE_BY_USER_ID_QUERY = "DELETE FROM TwoFactorVerification WHERE user_id= :id";
    public static final String INSERT_ACCOUNT_VERIFICATION_CODE_QUERY = "INSERT INTO TwoFactorVerification (user_id, code, expiration_date) VALUES (:user_id, :code, :expiration_date)";
    public static final String SELECT_USER_BY_USER_CODE_QUERY = "SELECT * FROM Users WHERE id = (SELECT user_id FROM TwoFactorVerification WHERE code = :code)";
    public static final String DELETE_FROM_TWO_FACTOR_VERIFICATION_QUERY = "DELETE FROM TwoFactorVerification WHERE code = :code";
    public static final String SELECT_EXPIRATION_DATE_BY_CODE_QUERY = "SELECT expiration_date < NOW() FROM TwoFactorVerification WHERE code = :code";
}
