package com.codelovers.quanonghau.service.impl;

import com.codelovers.quanonghau.entity.UserImage;
import com.codelovers.quanonghau.repository.UserImageRepository;
import com.codelovers.quanonghau.service.UserImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserImageServiceImpl implements UserImageService {

    @Autowired
    UserImageRepository userImageRepo;

    @Override
    public void deleteUserImage(UserImage userImage) {
        userImageRepo.delete(userImage);
    }

    @Override
    public UserImage saveUserImage(UserImage userImage) {
        return userImageRepo.save(userImage);
    }

    @Override
    public UserImage findByUserId(Integer userId) {

        return userImageRepo.findByUserId(userId);
    }
}
