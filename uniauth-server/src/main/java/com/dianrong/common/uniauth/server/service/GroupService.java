package com.dianrong.common.uniauth.server.service;

import com.dianrong.common.uniauth.common.bean.InfoName;
import com.dianrong.common.uniauth.common.bean.dto.GroupDto;
import com.dianrong.common.uniauth.common.bean.dto.PageDto;
import com.dianrong.common.uniauth.common.bean.dto.RoleDto;
import com.dianrong.common.uniauth.common.bean.dto.UserDto;
import com.dianrong.common.uniauth.common.bean.request.GroupParam;
import com.dianrong.common.uniauth.common.cons.AppConstants;
import com.dianrong.common.uniauth.server.data.entity.*;
import com.dianrong.common.uniauth.server.data.entity.ext.UserExt;
import com.dianrong.common.uniauth.server.data.mapper.*;
import com.dianrong.common.uniauth.server.exp.AppException;
import com.dianrong.common.uniauth.server.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.security.acl.Group;
import java.util.*;


/**
 * Created by Arc on 14/1/16.
 */
@Service
public class GroupService {
    @Autowired
    private GrpMapper grpMapper;
    @Autowired
    private GrpPathMapper grpPathMapper;
    @Autowired
    private GrpRoleMapper grpRoleMapper;
    @Autowired
    private UserGrpMapper userGrpMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;

    public PageDto<GroupDto> searchGroup(Integer id, String name, String code, String description, Byte status, Integer pageNumber, Integer pageSize) {

        if(id != null) {
            GroupDto groupDto = BeanConverter.convert(grpMapper.selectByPrimaryKey(id));
            if(groupDto != null) {
                return new PageDto<>(0, 1, 1, Arrays.asList(groupDto));
            } else {
                return null;
            }
        }

        if(code != null) {
            GrpExample grpExample = new GrpExample();
            grpExample.createCriteria().andCodeEqualTo(code);
            List<Grp> grps = grpMapper.selectByExample(grpExample);
            if(!CollectionUtils.isEmpty(grps)) {
                GroupDto groupDto = BeanConverter.convert(grps.get(0));
                return new PageDto<>(0, 1, 1, Arrays.asList(groupDto));
            } else {
                return null;
            }
        }

        if(pageNumber == null || pageSize == null) {
            throw new AppException(InfoName.VALIDATE_FAIL, UniBundle.getMsg("common.parameter.empty", "pageNumber, pageSize"));
        }

        GrpExample grpExample = new GrpExample();
        grpExample.setOrderByClause("create_date desc");
        grpExample.setPageOffSet(pageNumber * pageSize);
        grpExample.setPageSize(pageSize);
        GrpExample.Criteria criteria = grpExample.createCriteria();
        if(name != null) {
            criteria.andNameLike("%" + name + "%");
        }
        if(description != null) {
            criteria.andDescriptionLike("%" + description + "%");
        }
        if(status != null) {
            criteria.andStatusEqualTo(status);
        }
        List<Grp> grps = grpMapper.selectByExample(grpExample);
        if(!CollectionUtils.isEmpty(grps)) {
            int count = grpMapper.countByExample(grpExample);
            List<GroupDto> groupDtos = new ArrayList<>();
            for(Grp grp : grps) {
                groupDtos.add(BeanConverter.convert(grp));
            }
            return new PageDto<>(pageNumber,pageSize,count,groupDtos);
        } else {
            return null;
        }

    }

    @Transactional
    public GroupDto createDescendantGroup(GroupParam groupParam) {
        Integer targetGroupId = groupParam.getTargetGroupId();
        String groupCode = groupParam.getCode();
        if(targetGroupId == null || groupCode == null) {
            throw new AppException(InfoName.VALIDATE_FAIL, UniBundle.getMsg("common.parameter.empty", "targetGroupId, groupCode"));
        }
        GrpExample grpExample = new GrpExample();
        grpExample.createCriteria().andCodeEqualTo(groupCode);
        List<Grp> grps = grpMapper.selectByExample(grpExample);
        if(!CollectionUtils.isEmpty(grps)) {
            throw new AppException(InfoName.VALIDATE_FAIL, UniBundle.getMsg("group.parameter.code", groupCode));
        }
        Grp grp = BeanConverter.convert(groupParam);
        Date now = new Date();
        grp.setStatus(AppConstants.ZERO_Byte);
        grp.setCreateDate(now);
        grp.setLastUpdate(now);
        grpMapper.insert(grp);
        GrpPath grpPath = new GrpPath();
        grpPath.setDeepth(AppConstants.ZERO_Byte);
        grpPath.setDescendant(grp.getId());
        grpPath.setAncestor(targetGroupId);
        grpPathMapper.insertNewNode(grpPath);
        Integer count = grpMapper.selectNameCountBySameLayerGrpId(grp.getId());
        if(count !=null && count > 1) {
            throw new AppException(InfoName.VALIDATE_FAIL, UniBundle.getMsg("group.parameter.name", grp.getName()));
        }
        return BeanConverter.convert(grp);
    }

    @Transactional
    public void deleteGroup(Integer groupId) {
        GrpPathExample grpPathAncestorExample = new GrpPathExample();
        grpPathAncestorExample.createCriteria().andAncestorEqualTo(groupId);
        int desOfDesCount = grpPathMapper.countByExample(grpPathAncestorExample);
        if(desOfDesCount > 1) {
            throw new AppException(InfoName.VALIDATE_FAIL, UniBundle.getMsg("group.parameter.delgroup"));
        }
        // cascading delete the users in group and the roles on group.
        UserGrpExample userGrpExample = new UserGrpExample();
        userGrpExample.createCriteria().andGrpIdEqualTo(groupId);
        userGrpMapper.deleteByExample(userGrpExample);
        GrpRoleExample grpRoleExample = new GrpRoleExample();
        grpRoleExample.createCriteria().andGrpIdEqualTo(groupId);
        grpRoleMapper.deleteByExample(grpRoleExample);
        GrpPathExample grpPathDescendantExample = new GrpPathExample();
        grpPathDescendantExample.createCriteria().andDescendantEqualTo(groupId);
        grpPathMapper.deleteByExample(grpPathDescendantExample);
        grpMapper.deleteByPrimaryKey(groupId);
    }

    @Transactional
    public GroupDto updateGroup(Integer groupId, String groupCode, String groupName, Byte status, String description) {
        Grp grp = grpMapper.selectByPrimaryKey(groupId);
        CheckEmpty.checkEmpty(groupId, "groupId");
        CheckEmpty.checkEmpty(groupCode, "groupCode");
        if(status != null) {
            ParamCheck.checkStatus(status);
        }
        if(grp == null) {
            throw new AppException(InfoName.VALIDATE_FAIL, UniBundle.getMsg("common.entity.notfound", groupId, Grp.class.getSimpleName()));
        }
        grp.setName(groupName);
        grp.setStatus(status);
        grp.setDescription(description);
        grp.setCode(groupCode);
        grp.setLastUpdate(new Date());
        grpMapper.updateByPrimaryKeySelective(grp);
        Integer count  = grpMapper.selectNameCountBySameLayerGrpId(grp.getId());
        if(count !=null && count > 1) {
            throw new AppException(InfoName.VALIDATE_FAIL, UniBundle.getMsg("group.parameter.name", grp.getName()));
        }
        return BeanConverter.convert(grp);
    }

    @Transactional
    public void addUsersIntoGroup(Integer groupId, List<Long> userIds, Boolean normalMember) {
        if(groupId == null || CollectionUtils.isEmpty(userIds)) {
            throw new AppException(InfoName.VALIDATE_FAIL, UniBundle.getMsg("common.parameter.empty", "groupId, userIds"));
        }
        UserGrpExample userGrpExample = new UserGrpExample();
        List<UserGrpKey> userGrpKeys;
        if(normalMember == null || normalMember) {
            userGrpExample.createCriteria().andGrpIdEqualTo(groupId).andTypeEqualTo(AppConstants.ZERO_Byte);
            userGrpKeys = userGrpMapper.selectByExample(userGrpExample);
        } else {
            userGrpExample.createCriteria().andGrpIdEqualTo(groupId).andTypeEqualTo(AppConstants.ONE_Byte);
            userGrpKeys = userGrpMapper.selectByExample(userGrpExample);
        }
        Set<Long> userIdSet = new HashSet<>();
        if(!CollectionUtils.isEmpty(userGrpKeys)) {
            for (UserGrpKey userGrpKey : userGrpKeys) {
                userIdSet.add(userGrpKey.getUserId());
            }
        }
        for(Long userId : userIds) {
            if(!userIdSet.contains(userId)) {
                UserGrp userGrp = new UserGrp();
                userGrp.setGrpId(groupId);
                userGrp.setUserId(userId);
                if(normalMember == null || normalMember) {
                    userGrp.setType((byte)0);
                } else {
                    userGrp.setType((byte)1);
                }
                userGrpMapper.insert(userGrp);
            }
        }
    }

    @Transactional
    public void removeUsersFromGroup(Integer groupId, List<Long> userIds, Boolean normalMember) {
        if(groupId == null || CollectionUtils.isEmpty(userIds)) {
            throw new AppException(InfoName.VALIDATE_FAIL, UniBundle.getMsg("common.parameter.empty", "groupId, userIds"));
        }
        UserGrpExample userGrpExample = new UserGrpExample();
        List<UserGrpKey> userGrpKeys;
        if(normalMember == null || normalMember) {
            userGrpExample.createCriteria().andGrpIdEqualTo(groupId).andTypeEqualTo(AppConstants.ZERO_byte);
            userGrpKeys = userGrpMapper.selectByExample(userGrpExample);
        } else {
            userGrpExample.createCriteria().andGrpIdEqualTo(groupId).andTypeEqualTo(AppConstants.ONE_byte);
            userGrpKeys = userGrpMapper.selectByExample(userGrpExample);
        }
        Set<Long> userIdSet = new HashSet<>();
        if(!CollectionUtils.isEmpty(userGrpKeys)) {
            for (UserGrpKey userGrpKey : userGrpKeys) {
                userIdSet.add(userGrpKey.getUserId());
            }
        }
        for(Long userId : userIds) {
            if(userIdSet.contains(userId)) {
                UserGrpExample userGrpExample1 = new UserGrpExample();
                UserGrpExample.Criteria criteria = userGrpExample1.createCriteria().andGrpIdEqualTo(groupId).andUserIdEqualTo(userId);
                if(normalMember == null || normalMember) {
                    criteria.andTypeEqualTo(AppConstants.ZERO_Byte);
                } else {
                    criteria.andTypeEqualTo(AppConstants.ONE_Byte);
                }
                userGrpMapper.deleteByExample(userGrpExample1);
            }
        }
    }

    @Transactional
    public void saveRolesToGroup(Integer groupId, List<Integer> roleIds) {
        if(groupId == null || CollectionUtils.isEmpty(roleIds)) {
            throw new AppException(InfoName.VALIDATE_FAIL, UniBundle.getMsg("common.parameter.empty", "groupId, roleIds"));
        }
        GrpRoleExample grpRoleExample = new GrpRoleExample();
        grpRoleExample.createCriteria().andGrpIdEqualTo(groupId);
        List<GrpRoleKey> grpRoleKeys = grpRoleMapper.selectByExample(grpRoleExample);
        Set<Integer> roleIdSet = new TreeSet<>();
        if(!CollectionUtils.isEmpty(grpRoleKeys)) {
            for(GrpRoleKey grpRoleKey : grpRoleKeys) {
                roleIdSet.add(grpRoleKey.getRoleId());
            }
        }
        for(Integer roleId : roleIds) {
            if(!roleIdSet.contains(roleId)) {
                GrpRoleKey grpRoleKey = new GrpRoleKey();
                grpRoleKey.setGrpId(groupId);
                grpRoleKey.setRoleId(roleId);
                grpRoleMapper.insert(grpRoleKey);
            }
        }
    }

    public List<RoleDto> getAllRolesToGroupAndDomain(Integer groupId, Integer domainId) {
        if(groupId == null || domainId == null) {
            throw new AppException(InfoName.VALIDATE_FAIL, UniBundle.getMsg("common.parameter.empty", "groupId, domainId"));
        }
        if(grpMapper.selectByPrimaryKey(groupId) == null) {
            return null;
        }
        GrpRoleExample grpRoleExample = new GrpRoleExample();
        grpRoleExample.createCriteria().andGrpIdEqualTo(groupId);
        List<GrpRoleKey> grpRoleKeys = grpRoleMapper.selectByExample(grpRoleExample);
        Set<Integer> checkedRoleIds = new HashSet<>();
        if(!CollectionUtils.isEmpty(grpRoleKeys)) {
            for(GrpRoleKey grpRoleKey : grpRoleKeys) {
                checkedRoleIds.add(grpRoleKey.getRoleId());
            }
        }
        RoleExample roleExample = new RoleExample();
        roleExample.createCriteria().andDomainIdEqualTo(domainId).andStatusEqualTo(AppConstants.ZERO_Byte);
        List<Role> roles = roleMapper.selectByExample(roleExample);
        if(!CollectionUtils.isEmpty(roles)) {
            List<RoleDto> roleDtos = new ArrayList<>();
            for(Role role : roles) {
                if(role.getStatus().equals(AppConstants.ONE_Byte)) {
                    continue;
                }
                RoleDto roleDto = BeanConverter.convert(role);
                if(checkedRoleIds.contains(roleDto.getId())) {
                    roleDto.setChecked(Boolean.TRUE);
                } else {
                    roleDto.setChecked(Boolean.FALSE);
                }
                roleDtos.add(roleDto);
            }
            return roleDtos;
        } else {
            return null;
        }
    }

    public List<UserDto> getGroupOwners(Integer groupId) {
        List<User> users = userMapper.getGroupOwners(groupId);
        if(!CollectionUtils.isEmpty(users)) {
            List<UserDto> userDtos = new ArrayList<>();
            for(User user : users) {
                if(user.getStatus().equals(AppConstants.ONE_Byte)) {
                    continue;
                }
                UserDto userDto = BeanConverter.convert(user);
                userDtos.add(userDto);
            }
            return userDtos;
        } else {
            return null;
        }
    }

    public GroupDto getGroupTree(Integer groupId, String groupCode, Boolean onlyShowGroup, Byte userGroupType, Integer roleId) {
        Grp rootGrp;
        if(groupCode == null && (groupId == null || Integer.valueOf(-1).equals(groupId))) {
            GrpExample grpExample = new GrpExample();
            grpExample.createCriteria().andCodeEqualTo(AppConstants.GRP_ROOT);
            rootGrp = grpMapper.selectByExample(grpExample).get(0);
        } else if(groupCode != null && groupId != null) {
            GrpExample grpExample = new GrpExample();
            grpExample.createCriteria().andCodeEqualTo(groupCode).andStatusEqualTo(AppConstants.ZERO_Byte);
            List<Grp> grps = grpMapper.selectByExample(grpExample);
            Grp grp = grpMapper.selectByPrimaryKey(groupId);
            if(grp == null) {
                throw new AppException(InfoName.VALIDATE_FAIL, UniBundle.getMsg("common.entity.notfound", groupId, Grp.class.getSimpleName()));
            }
            if (CollectionUtils.isEmpty(grps)) {
                throw new AppException(InfoName.VALIDATE_FAIL, UniBundle.getMsg("common.entity.code.notfound", groupCode, Grp.class.getSimpleName()));
            }
            if(!grp.getId().equals(grps.get(0).getId())) {
                throw new AppException(InfoName.VALIDATE_FAIL, UniBundle.getMsg("group.parameter.code.id.dif", groupCode, groupId));
            }
            rootGrp = grp;
        } else if(groupCode != null && groupId == null) {
            GrpExample grpExample = new GrpExample();
            grpExample.createCriteria().andCodeEqualTo(groupCode).andStatusEqualTo(AppConstants.ZERO_Byte);
            List<Grp> grps = grpMapper.selectByExample(grpExample);
            if (CollectionUtils.isEmpty(grps)) {
                throw new AppException(InfoName.VALIDATE_FAIL, UniBundle.getMsg("common.entity.code.notfound", groupCode, Grp.class.getSimpleName()));
            }
            rootGrp = grps.get(0);
        } else {
            //else if(groupCode == null && groupId != null)
            rootGrp = grpMapper.selectByPrimaryKey(groupId);
            if(rootGrp == null || !AppConstants.ZERO_Byte.equals(rootGrp.getStatus())) {
                throw new AppException(InfoName.VALIDATE_FAIL, UniBundle.getMsg("common.entity.notfound", groupId, Grp.class.getSimpleName()));
            }
        }
        Integer realGroupId = rootGrp.getId();
        List<Grp> grps = grpMapper.getGroupTree(realGroupId);
        if(!CollectionUtils.isEmpty(grps)) {
            List<HashMap<String,Integer>> descendantAncestorPairs = grpMapper.getGroupTreeLinks(realGroupId);
            Map<Integer, GroupDto> idGroupDtoPair = new HashMap();
            for(Grp grp : grps) {
                GroupDto groupDto = new GroupDto().setId(grp.getId()).setCode(grp.getCode()).setName(grp.getName())
                        .setDescription(grp.getDescription());
                idGroupDtoPair.put(groupDto.getId(), groupDto);
            }

            // construct the tree
            if(!CollectionUtils.isEmpty(descendantAncestorPairs)) {
                for(HashMap<String,Integer> descendantAncestorPair : descendantAncestorPairs) {
                    Integer descendantId = descendantAncestorPair.get("descendant");
                    Integer ancestorId = descendantAncestorPair.get("ancestor");
                    GroupDto ancestorDto = idGroupDtoPair.get(ancestorId);
                    GroupDto descendantDto = idGroupDtoPair.get(descendantId);
                    List<GroupDto> groupDtos = ancestorDto.getGroups();
                    if(groupDtos == null) {
                        groupDtos = new ArrayList<>();
                        ancestorDto.setGroups(groupDtos);
                    }
                    groupDtos.add(descendantDto);
                }
            }

            if(roleId != null) {
                //role checked on group
                GrpRoleExample grpRoleExample = new GrpRoleExample();
                grpRoleExample.createCriteria().andRoleIdEqualTo(roleId).andGrpIdIn(new ArrayList<Integer>(idGroupDtoPair.keySet()));
                List<GrpRoleKey> grpRoleKeys = grpRoleMapper.selectByExample(grpRoleExample);
                if(!CollectionUtils.isEmpty(grpRoleKeys)) {
                    for (GrpRoleKey grpRoleKey : grpRoleKeys) {
                        Integer checkedGroupId = grpRoleKey.getGrpId();
                        idGroupDtoPair.get(checkedGroupId).setRoleChecked(Boolean.TRUE);
                    }
                }
            }


            if(onlyShowGroup != null && !onlyShowGroup) {
                CheckEmpty.checkEmpty(userGroupType, "users' type in the group");
                Map<String, Object> groupIdAndUserType = new HashMap<String, Object>();
                groupIdAndUserType.put("id", realGroupId);
                groupIdAndUserType.put("userGroupType", userGroupType);
                List<UserExt> userExts = userMapper.getUsersByParentGrpIdByUserType(groupIdAndUserType);
                // construct the users on the tree
                if(!CollectionUtils.isEmpty(userExts)) {
                    //role checked on users
                    Set<Long> userIdsOnRole = null;
                    if(roleId != null) {
                        UserRoleExample userRoleExample = new UserRoleExample();
                        userRoleExample.createCriteria().andRoleIdEqualTo(roleId);
                        List<UserRoleKey> userRoleKeys = userRoleMapper.selectByExample(userRoleExample);
                        if(!CollectionUtils.isEmpty(userRoleKeys)) {
                            userIdsOnRole = new TreeSet<>();
                            for (UserRoleKey userRoleKey : userRoleKeys) {
                                userIdsOnRole.add(userRoleKey.getUserId());
                            }
                        }
                    }
                    for(UserExt userExt : userExts) {
                        UserDto userDto = new UserDto().setEmail(userExt.getEmail()).setId(userExt.getId()).setUserGroupType(userExt.getUserGroupType());
                        GroupDto groupDto = idGroupDtoPair.get(userExt.getGroupId());
                        List<UserDto> userDtos = groupDto.getUsers();
                        if(userDtos == null) {
                            userDtos = new ArrayList<>();
                            groupDto.setUsers(userDtos);
                        }
                        userDtos.add(userDto);
                        if(userIdsOnRole != null && userIdsOnRole.contains(userDto.getId())) {
                            userDto.setRoleChecked(Boolean.TRUE);
                        }
                    }

                }
            }

            return idGroupDtoPair.get(realGroupId);
        } else {
            return null;
        }
    }
}
