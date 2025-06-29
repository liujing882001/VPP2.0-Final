package com.example.vvpservice.applicationCenter.model;

import com.example.vvpdomain.entity.Menu;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
public class ApplicationResponse {
	private String applicationName;

	private String link;

	private String icon;

	private int isFrame;

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private String applicationNameEn;

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private List<ApplicationResponse> child = new ArrayList<>();

	public ApplicationResponse(Menu menu) {
		this.applicationName = menu.getMenuName();
		this.icon = (menu.getIconLink() == null) ? null : ServletUriComponentsBuilder.fromCurrentContextPath().toUriString() + menu.getIconLink();
		this.link = menu.getComponent();
		this.applicationNameEn = menu.getMenuNameEn();
		this.isFrame = menu.getIsFrame();
	}
}
