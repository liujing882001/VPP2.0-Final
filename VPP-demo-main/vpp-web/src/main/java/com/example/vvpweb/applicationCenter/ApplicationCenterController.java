package com.example.vvpweb.applicationCenter;

import com.example.vvpcommom.ResponseResult;
import com.example.vvpcommom.UserLoginToken;
import com.example.vvpservice.applicationCenter.ApplicationCenterService;
import com.example.vvpservice.applicationCenter.model.ApplicationResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/applicationCenter")
@CrossOrigin
public class ApplicationCenterController {

	@Autowired
	ApplicationCenterService applicationCenterService;

	@ApiOperation("功能列表动态查询接口")
	@UserLoginToken
	@RequestMapping(value = "/queryApplication", method = {RequestMethod.GET})
	public ResponseResult<List<ApplicationResponse>> queryApplication(@RequestParam(name = "name", required = false) String name,
	                                                                @RequestParam(name = "type", required = false) String type,HttpServletRequest request) {
		try {
			List<ApplicationResponse> responseList = applicationCenterService.queryApplication(name, type,request);
			return ResponseResult.success(responseList);
		} catch (Exception e) {
			return ResponseResult.error(e.getMessage());
		}
	}

	@ApiOperation("功能列表动态查询接口")
	@UserLoginToken
	@RequestMapping(value = "/queryAllApplication", method = {RequestMethod.GET})
	public ResponseResult<List<ApplicationResponse>> getAllApplication(HttpServletRequest request) {
		try {
			List<ApplicationResponse> responseList = applicationCenterService.queryAllApplication(request);
			return ResponseResult.success(responseList);
		} catch (Exception e) {
			return ResponseResult.error(e.getMessage());
		}
	}

	@PutMapping(value = "/addApplicationLog")
	public ResponseResult<String> addApplicationLog(HttpServletRequest request, @RequestParam String name) {
		try {
			applicationCenterService.addApplicationLog(request, name);
			return ResponseResult.success("记录添加成功");
		} catch (Exception e) {
			return ResponseResult.error(e.getMessage());
		}
	}

	@GetMapping("queryApplicationLog")
	public ResponseResult<List<ApplicationResponse>> queryApplicationLog(HttpServletRequest request) {
		try {
			List<ApplicationResponse> res = applicationCenterService.queryApplicationLog(request);
			return ResponseResult.success(res);
		} catch (Exception e) {
			return ResponseResult.error(e.getMessage());
		}
	}

	@PostMapping(value = "/icon/upload")
	public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("appName") String applicationName) {
		try {
			if (file.isEmpty()) {
				return ResponseEntity.badRequest().body("No file uploaded.");
			}

			// 获取文件名并构造文件路径
			String filename = URLDecoder.decode(applicationName,StandardCharsets.UTF_8.name());
			String originalFilename = file.getOriginalFilename();
			String mediaType = ".png";
			if (originalFilename != null && originalFilename.contains(".")) {
				mediaType = originalFilename.substring(originalFilename.lastIndexOf("."));
			}
			Path targetLocation = Paths.get(ApplicationCenterService.ICON_DIR, filename + mediaType);

			// 创建目标目录
			Files.createDirectories(targetLocation.getParent());

			// 将文件保存到指定目录
			file.transferTo(targetLocation);
			applicationCenterService.saveIconLink(applicationName, filename + mediaType);

			return ResponseEntity.ok("File uploaded successfully: " + filename);
		} catch (Exception e) {
			return ResponseEntity.status(500).body("File upload failed: " + e.getMessage());
		}
	}

	@GetMapping("/icon/{filename}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
		try {
			String decodedFilename = URLDecoder.decode(filename, StandardCharsets.UTF_8.name());
			// 获取文件路径
			Path filePath = Paths.get(ApplicationCenterService.ICON_DIR, decodedFilename);
			Resource resource = new FileSystemResource(filePath);

			// 检查文件是否存在
			if (resource.exists()) {
				String contentType = Files.probeContentType(filePath);
				if (contentType == null) {
					contentType = "application/octet-stream"; // 默认 MIME 类型
				}

				return ResponseEntity.ok()
						.contentType(MediaType.parseMediaType(contentType))
						.body(resource);
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			return ResponseEntity.status(500).body(null);
		}
	}


}
