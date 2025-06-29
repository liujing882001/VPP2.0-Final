package com.example.vvpservice.prouser.service;
import java.util.Collections;

import com.example.vvpcommom.Enum.ElectricityBillNodeEnum;
import com.example.vvpcommom.Enum.NodePostTypeEnum;
import com.example.vvpcommom.RequestHeaderContext;
import com.example.vvpdomain.NodeRepository;
import com.example.vvpdomain.StationNodeRepository;
import com.example.vvpdomain.SysDictTypeRepository;
import com.example.vvpdomain.UserRepository;
import com.example.vvpdomain.entity.Node;
import com.example.vvpdomain.entity.Role;
import com.example.vvpdomain.entity.StationNode;
import com.example.vvpdomain.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.stream.Stream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.EMPTY_LIST;


@Service
public class UserServiceImpl implements IUserService {

	private static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private NodeRepository nodeRepository;

	@Autowired
	private SysDictTypeRepository sysDictTypeRepository;
	@Resource
	private StationNodeRepository stationNodeRepository;


	@Override
	public List<String> getAllowNodeIds() {
		String userId = RequestHeaderContext.getInstance().getUserId();
		if (userId == null) {
			return EMPTY_LIST;
//            return nodeRepository.findAll().stream().map(Node::getNodeId).collect(Collectors.toList());
		}

		Optional<User> byId = userRepository.findById(userId);
		if (byId.isPresent()) {
			User user = byId.get();

			Role role = user.getRole();

			if (role == null) {
				return EMPTY_LIST;
			}

			//用户为系统管理员 或者普通管理员
			if (1 == role.getRoleKey() || 2 == role.getRoleKey()) {
				return nodeRepository.findAll().stream().map(Node::getNodeId).collect(Collectors.toList());
			}
			//用户为电力用户或者负荷集成商
			if (3 == role.getRoleKey() || 4 == role.getRoleKey() || 5 == role.getRoleKey()) {
				return user.getNodeList().stream().map(Node::getNodeId).collect(Collectors.toList());
			}

		} else {
			throw new IllegalArgumentException("获取用户权限下的节点列表,用户ID" + userId + "不存在");
		}

		return EMPTY_LIST;
	}

	@Override
	public List<String> getAllowNodeIds(String userId) {
		if (userId == null) {
			return EMPTY_LIST;
//            return nodeRepository.findAll().stream().map(Node::getNodeId).collect(Collectors.toList());
		}

		Optional<User> byId = userRepository.findById(userId);
		if (byId.isPresent()) {
			User user = byId.get();

			Role role = user.getRole();

			if (role == null) {
				return EMPTY_LIST;
			}

			//用户为系统管理员 或者普通管理员
			if (1 == role.getRoleKey() || 2 == role.getRoleKey()) {
				return nodeRepository.findAll().stream().map(Node::getNodeId).collect(Collectors.toList());
			}
			//用户为电力用户或者负荷集成商
			if (3 == role.getRoleKey() || 4 == role.getRoleKey() || 5 == role.getRoleKey()) {
				return user.getNodeList().stream().map(Node::getNodeId).collect(Collectors.toList());
			}

		} else {
			throw new IllegalArgumentException("获取用户权限下的节点列表,用户ID" + userId + "不存在");
		}

		return EMPTY_LIST;
	}

	@Override
	public List<String> getRunAllowNodeIds() {
		String userId = RequestHeaderContext.getInstance().getUserId();
		if (userId == null) {
			return EMPTY_LIST;
//            return nodeRepository.findAll().stream().map(Node::getNodeId).collect(Collectors.toList());
		}

		Optional<User> byId = userRepository.findById(userId);
		if (byId.isPresent()) {
			User user = byId.get();

			Role role = user.getRole();

			if (role == null) {
				return EMPTY_LIST;
			}

			//用户为系统管理员 或者普通管理员
			if (1 == role.getRoleKey() || 2 == role.getRoleKey()) {
				return stationNodeRepository.findAllNodeInOperation().stream().map(StationNode::getStationId).collect(Collectors.toList());
			}
			//用户为电力用户或者负荷集成商
			if (3 == role.getRoleKey() || 4 == role.getRoleKey() || 5 == role.getRoleKey()) {
				return user.getNodeList().stream().map(Node::getNodeId).collect(Collectors.toList());
			}

		} else {
			throw new IllegalArgumentException("获取用户权限下的节点列表,用户ID" + userId + "不存在");
		}

		return EMPTY_LIST;
	}

	@Override
	public List<String> getAllowPvNodeIds() {
		List<String> result = new ArrayList<>();
		List<Node> all = nodeRepository.findAllByNodeIdInAndNodePostType(getAllowNodeIds(), NodePostTypeEnum.pv.getNodePostType());
		all.forEach(e -> result.add(e.getNodeId()));
		return result;
	}

	@Override
	public List<String> getAllowRunPvNodeIds() {
		return stationNodeRepository.findAllByNodeIdsAndStationType(getAllowNodeIds(), NodePostTypeEnum.pv.getNodePostType());
	}

	@Override
	public List<String> getAllowLoadNodeIds() {
		List<String> responses = new ArrayList<>();
		List<Node> all = nodeRepository.findAllByNodeIdInAndNodePostType(getAllowNodeIds(), NodePostTypeEnum.load.getNodePostType());
		all.forEach(e -> responses.add(e.getNodeId()));
		return responses;
	}

	@Override
	public List<String> getAllowRunLoadNodeIds() {
		return stationNodeRepository.findAllByNodeIdsAndStationType(getAllowNodeIds(), NodePostTypeEnum.load.getNodePostType());
	}

	@Override
	public List<String> getAllLoadNodeIds() {
		List<String> responses = new ArrayList<>();
		List<Node> all = nodeRepository.findAllByNodePostType(NodePostTypeEnum.load.getNodePostType());
		all.forEach(e -> responses.add(e.getNodeId()));
		return responses;
	}

	@Override
	public List<String> getAllChargingPileLoadNodeIds() {
		List<String> responses = new ArrayList<>();
		List<Node> all = nodeRepository.findAllByNodeType_NodeTypeIdAndNodePostType(ElectricityBillNodeEnum.chargingPile.getNodePostType(),
                ElectricityBillNodeEnum.chargingPile.getNodePostType());
		all.forEach(e -> responses.add(e.getNodeId()));
		return responses;
	}

	@Override
	public List<String> getAllRunChargingPileLoadNodeIds() {
		return stationNodeRepository.findAllByNodeIdsAndStationType(getAllowNodeIds(), ElectricityBillNodeEnum.chargingPile.getNodePostType(),
                ElectricityBillNodeEnum.chargingPile.getNodePostType());
	}

	@Override
	public List<String> getAllowStorageEnergyNodeIds() {
		List<String> result = new ArrayList<>();
		List<Node> all = nodeRepository.findAllByNodeIdInAndNodePostType(getAllowNodeIds(), NodePostTypeEnum.storageEnergy.getNodePostType());
		all.forEach(e -> result.add(e.getNodeId()));
		return result;
	}

	@Override
	public List<String> getAllowRunStorageEnergyNodeIds() {
		return stationNodeRepository.findAllByNodeIdsAndStationType(getAllowNodeIds(), NodePostTypeEnum.storageEnergy.getNodePostType());
	}

	@Override
	public boolean isManger() {
		String userId = RequestHeaderContext.getInstance().getUserId();
		if (userId == null) {
			return false;
		}

		Optional<User> byId = userRepository.findById(userId);
		if (byId.isPresent()) {
			User user = byId.get();
			Role role = user.getRole();
			if (role == null) {
				return false;
			}
			//用户为系统管理员 或者普通管理员
			return 1 == role.getRoleKey() || 2 == role.getRoleKey();
		}
		return false;
	}
}
