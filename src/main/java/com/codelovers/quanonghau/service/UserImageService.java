package com.codelovers.quanonghau.service;

import com.codelovers.quanonghau.entity.UserImage;

public interface UserImageService {

    void deleteUserImage(UserImage userImage);

    UserImage saveUserImage(UserImage userImage);

    UserImage findByUserId(Integer userId);
}
