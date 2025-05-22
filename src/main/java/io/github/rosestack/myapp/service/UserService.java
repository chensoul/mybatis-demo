package io.github.rosestack.myapp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.rosestack.myapp.domain.User;
import io.github.rosestack.myapp.model.UserDTO;
import io.github.rosestack.myapp.repository.UserRepository;
import io.github.rosestack.myapp.util.NotFoundException;
import io.github.rosestack.myapp.util.PageUtils;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService extends ServiceImpl<UserRepository, User> {
    public Page<UserDTO> findAllUsers(String filter, Pageable pageable) {
        IPage<User> page = null;
        if (filter != null) {
            page = baseMapper.selectPage(
                    PageUtils.fromPageable(pageable),
                    Wrappers.<User>lambdaQuery().like(User::getName, filter));
        } else {
            page = page(PageUtils.fromPageable(pageable));
        }
        return new PageImpl<>(
                page.getRecords().stream()
                        .map(user -> mapToDTO(user, new UserDTO()))
                        .toList(),
                pageable,
                page.getTotal());
    }

    public UserDTO findUserById(Long id) {
        return Optional.ofNullable(getById(id))
                .map(user -> mapToDTO(user, new UserDTO()))
                .orElseThrow(NotFoundException::new);
    }

    @Transactional(rollbackFor = Exception.class)
    public UserDTO saveUser(final UserDTO userDTO) {
        final User user = new User();
        mapToEntity(userDTO, user);
        baseMapper.insert(user);
        return mapToDTO(user, userDTO);
    }

    @Transactional(rollbackFor = Exception.class)
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = Optional.of(baseMapper.selectById(id)).orElseThrow(NotFoundException::new);
        mapToEntity(userDTO, user);
        baseMapper.updateById(user);
        return mapToDTO(user, userDTO);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteUserById(Long id) {
        baseMapper.deleteById(id);
    }

    public boolean nameExists(String value) {
        return baseMapper.exists(Wrappers.<User>lambdaQuery().eq(User::getName, value));
    }

    private UserDTO mapToDTO(final User user, final UserDTO userDTO) {
        userDTO.setName(user.getName());
        userDTO.setId(user.getId());
        return userDTO;
    }

    private User mapToEntity(final UserDTO userDTO, final User user) {
        user.setName(userDTO.getName());
        return user;
    }
}
