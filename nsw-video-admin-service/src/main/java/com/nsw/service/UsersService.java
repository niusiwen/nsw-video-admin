package com.nsw.service;

import com.nsw.pojo.Users;
import com.nsw.utils.PagedResult;

public interface UsersService {

	PagedResult queryUsers(Users user, Integer page, Integer pageSize);

}
