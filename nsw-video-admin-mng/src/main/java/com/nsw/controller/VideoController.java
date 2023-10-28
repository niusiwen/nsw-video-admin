package com.nsw.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.nsw.bean.AdminUser;
import com.nsw.enums.VideoStatusEnum;
import com.nsw.pojo.Bgm;
import com.nsw.service.VideoService;
import com.nsw.utils.NswJSONResult;
import com.nsw.utils.PagedResult;

@Controller
@RequestMapping("video")
public class VideoController {
	
	@Autowired
	private VideoService videoService;
	
	@GetMapping("/showReportList")
	public String showReportList() {
		return "video/reportList";
	}
	
	@PostMapping("/reportList")
	@ResponseBody
	public PagedResult reportList(Integer page) {
		
		PagedResult result = videoService.queryReportList(page, 10);
		return result;
	}
	
	@PostMapping("/forbidVideo")
	@ResponseBody
	public NswJSONResult forbidVideo(String videoId) {
		
		videoService.updateVideoStatus(videoId, VideoStatusEnum.FORBID.value);
		return NswJSONResult.ok();
	}
	
	@GetMapping("/showBgmList")
	public String showBgmList() {
		return "video/bgmList";
	}

	@GetMapping("/showAddBgm")
	public String showAddBgm() {
		return "video/addBgm";
	}
	
	@PostMapping("/queryBgmList")
	@ResponseBody
	public PagedResult queryBgmList(Integer page) {
		
		return videoService.queryBgmList(page, 10);
	}
	
	@PostMapping("/addBgm")
	@ResponseBody
	public NswJSONResult addBgm(Bgm bgm) {
		
		videoService.addBgm(bgm);
		return NswJSONResult.ok();
	}
	
	@PostMapping("/delBgm")
	@ResponseBody
	public NswJSONResult delBgm(String bgmId) {
		
		videoService.deleteBgm(bgmId);
		return NswJSONResult.ok();
	}
	
	@PostMapping("/bgmUpload")
	@ResponseBody
	public NswJSONResult bgmUpload(@RequestParam("file") MultipartFile[] files) throws Exception {
		
		// 文件保存的命名空间
//		String fileSpace = File.separator + "imooc_videos_dev" + File.separator + "mvc-bgm";
		String fileSpace = "G:" + File.separator + "nsw_video_dev" + File.separator + "mvc-bgm";
		// 保存到数据库中的相对路径
		String uploadPathDB = File.separator + "bgm";
		
		FileOutputStream fileOutputStream = null;
		InputStream inputStream = null;
		try {
			if (files != null && files.length > 0) {
				
				String fileName = files[0].getOriginalFilename();
				if (StringUtils.isNotBlank(fileName)) {
					// 文件上传的最终保存路径
					String finalPath = fileSpace + uploadPathDB + File.separator + fileName;
					// 设置数据库保存的路径
					uploadPathDB += (File.separator + fileName);
					
					File outFile = new File(finalPath);
					if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
						// 创建父文件夹
						outFile.getParentFile().mkdirs();
					}
					
					fileOutputStream = new FileOutputStream(outFile);
					inputStream = files[0].getInputStream();
					IOUtils.copy(inputStream, fileOutputStream);
				}
				
			} else {
				return NswJSONResult.errorMsg("上传出错...");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return NswJSONResult.errorMsg("上传出错...");
		} finally {
			if (fileOutputStream != null) {
				fileOutputStream.flush();
				fileOutputStream.close();
			}
		}
		
		return NswJSONResult.ok(uploadPathDB);
	}
	
}
