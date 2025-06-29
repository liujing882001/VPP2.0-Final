package com.example.vvpweb.tree.controller;

import com.example.vvpcommom.PinyinUtils;
import com.example.vvpcommom.ResponseResult;
import com.example.vvpcommom.UserLoginToken;
import com.example.vvpservice.tree.model.StructTreeResponse;
import com.example.vvpservice.tree.service.ITreeLabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/tree")
@CrossOrigin
public class LabelController {

    @Autowired
    private ITreeLabelService iTreeLabelService;


    @UserLoginToken
    @RequestMapping(value = "areaLoadForestShortView", method = {RequestMethod.POST})
    public ResponseResult<List<StructTreeResponse>> areaLoadForestShortView() {

        List<StructTreeResponse> treeResponses = iTreeLabelService.areaLoadForestShortView();
        if (treeResponses != null && treeResponses.size() > 0) {
            Comparator comparator = Collator.getInstance(Locale.CHINA);
            Collections.sort(treeResponses, (p1, p2) -> comparator.compare(
                    PinyinUtils.converterToFirstSpell(p1.getTitle().substring(0, 1)).toLowerCase(),
                    PinyinUtils.converterToFirstSpell(p2.getTitle().substring(0, 1)).toLowerCase()));
        }
        return ResponseResult.success(treeResponses);
    }

    @UserLoginToken
    @RequestMapping(value = "runAreaLoadForestShortView", method = {RequestMethod.POST})
    public ResponseResult<List<StructTreeResponse>> runAreaLoadForestShortView() {

        List<StructTreeResponse> treeResponses = iTreeLabelService.runAreaLoadForestShortView();
        if (treeResponses != null && treeResponses.size() > 0) {
            Comparator comparator = Collator.getInstance(Locale.CHINA);
            Collections.sort(treeResponses, (p1, p2) -> comparator.compare(
                    PinyinUtils.converterToFirstSpell(p1.getTitle().substring(0, 1)).toLowerCase(),
                    PinyinUtils.converterToFirstSpell(p2.getTitle().substring(0, 1)).toLowerCase()));
        }
        return ResponseResult.success(treeResponses);
    }

    @UserLoginToken
    @RequestMapping(value = "typeLoadForestShortView", method = {RequestMethod.POST})
    public ResponseResult<List<StructTreeResponse>> typeLoadForestShortView() {

        List<StructTreeResponse> treeResponses = iTreeLabelService.typeLoadForestShortView();
        if (treeResponses != null && treeResponses.size() > 0) {
            Comparator comparator = Collator.getInstance(Locale.CHINA);
            Collections.sort(treeResponses, (p1, p2) -> comparator.compare(
                    PinyinUtils.converterToFirstSpell(p1.getTitle().substring(0, 1)).toLowerCase(),
                    PinyinUtils.converterToFirstSpell(p2.getTitle().substring(0, 1)).toLowerCase()));
        }
        return ResponseResult.success(treeResponses);
    }


    @UserLoginToken
    @RequestMapping(value = "areaPvForestShortView", method = {RequestMethod.POST})
    public ResponseResult<List<StructTreeResponse>> areaPvForestShortView() {

        List<StructTreeResponse> treeResponses = iTreeLabelService.areaPvForestShortView();
        if (treeResponses != null && treeResponses.size() > 0) {
            Comparator comparator = Collator.getInstance(Locale.CHINA);
            Collections.sort(treeResponses, (p1, p2) -> comparator.compare(
                    PinyinUtils.converterToFirstSpell(p1.getTitle().substring(0, 1)).toLowerCase(),
                    PinyinUtils.converterToFirstSpell(p2.getTitle().substring(0, 1)).toLowerCase()));

        }
        return ResponseResult.success(treeResponses);
    }

    @UserLoginToken
    @RequestMapping(value = "runAreaPvForestShortView", method = {RequestMethod.POST})
    public ResponseResult<List<StructTreeResponse>> runAreaPvForestShortView() {

        List<StructTreeResponse> treeResponses = iTreeLabelService.runAreaPvForestShortView();
        if (treeResponses != null && treeResponses.size() > 0) {
            Comparator comparator = Collator.getInstance(Locale.CHINA);
            Collections.sort(treeResponses, (p1, p2) -> comparator.compare(
                    PinyinUtils.converterToFirstSpell(p1.getTitle().substring(0, 1)).toLowerCase(),
                    PinyinUtils.converterToFirstSpell(p2.getTitle().substring(0, 1)).toLowerCase()));

        }
        return ResponseResult.success(treeResponses);
    }

    @UserLoginToken
    @RequestMapping(value = "typePvForestShortView", method = {RequestMethod.POST})
    public ResponseResult<List<StructTreeResponse>> typePvForestShortView() {

        List<StructTreeResponse> treeResponses = iTreeLabelService.typePvForestShortView();
        if (treeResponses != null && treeResponses.size() > 0) {
            Comparator comparator = Collator.getInstance(Locale.CHINA);
            Collections.sort(treeResponses, (p1, p2) -> comparator.compare(
                    PinyinUtils.converterToFirstSpell(p1.getTitle().substring(0, 1)).toLowerCase(),
                    PinyinUtils.converterToFirstSpell(p2.getTitle().substring(0, 1)).toLowerCase()));

        }
        return ResponseResult.success(treeResponses);
    }


    /**
     * 光伏节点查询
     */
    @UserLoginToken
    @RequestMapping(value = "pvNodeTree", method = {RequestMethod.POST})
    public ResponseResult<List<StructTreeResponse>> pvNodeTree() {
        List<StructTreeResponse> treeResponses = iTreeLabelService.pvNodeTree();
        if (treeResponses != null && treeResponses.size() > 0) {
            Comparator comparator = Collator.getInstance(Locale.CHINA);
            Collections.sort(treeResponses, (p1, p2) -> comparator.compare(
                    PinyinUtils.converterToFirstSpell(p1.getTitle().substring(0, 1)).toLowerCase(),
                    PinyinUtils.converterToFirstSpell(p2.getTitle().substring(0, 1)).toLowerCase()));

        }
        return ResponseResult.success(treeResponses);
    }

    /**
     * 运营中光伏节点查询
     */
    @UserLoginToken
    @RequestMapping(value = "runPvNodeTree", method = {RequestMethod.POST})
    public ResponseResult<List<StructTreeResponse>> runPvNodeTree() {
        List<StructTreeResponse> treeResponses = iTreeLabelService.runPvNodeTree();
        if (treeResponses != null && treeResponses.size() > 0) {
            Comparator comparator = Collator.getInstance(Locale.CHINA);
            Collections.sort(treeResponses, (p1, p2) -> comparator.compare(
                    PinyinUtils.converterToFirstSpell(p1.getTitle().substring(0, 1)).toLowerCase(),
                    PinyinUtils.converterToFirstSpell(p2.getTitle().substring(0, 1)).toLowerCase()));

        }
        return ResponseResult.success(treeResponses);
    }


    /**
     * 可调负荷点查询
     */
    @UserLoginToken
    @RequestMapping(value = "loadNodeTree", method = {RequestMethod.POST})
    public ResponseResult<List<StructTreeResponse>> loadNodeTree() {
        List<StructTreeResponse> treeResponses = iTreeLabelService.loadNodeTree();
        if (treeResponses != null && treeResponses.size() > 0) {
            Comparator comparator = Collator.getInstance(Locale.CHINA);
            Collections.sort(treeResponses, (p1, p2) -> comparator.compare(
                    PinyinUtils.converterToFirstSpell(p1.getTitle().substring(0, 1)).toLowerCase(),
                    PinyinUtils.converterToFirstSpell(p2.getTitle().substring(0, 1)).toLowerCase()));

        }
        return ResponseResult.success(treeResponses);
    }


    /**
     * 储能节点查询
     */
    @UserLoginToken
    @RequestMapping(value = "storageEnergyNodeTree", method = {RequestMethod.POST})
    public ResponseResult<List<StructTreeResponse>> storageEnergyNodeTree() {
        List<StructTreeResponse> treeResponses = iTreeLabelService.storageEnergyNodeTree();
        if (treeResponses != null && treeResponses.size() > 0) {
            Comparator comparator = Collator.getInstance(Locale.CHINA);
            Collections.sort(treeResponses, (p1, p2) -> comparator.compare(
                    PinyinUtils.converterToFirstSpell(p1.getTitle().substring(0, 1)).toLowerCase(),
                    PinyinUtils.converterToFirstSpell(p2.getTitle().substring(0, 1)).toLowerCase()));

        }
        return ResponseResult.success(treeResponses);
    }
    /**
     * 运营中储能节点查询
     */
    @UserLoginToken
    @RequestMapping(value = "runStorageEnergyNodeTree", method = {RequestMethod.POST})
    public ResponseResult<List<StructTreeResponse>> runStorageEnergyNodeTree() {
        List<StructTreeResponse> treeResponses = iTreeLabelService.runStorageEnergyNodeTree();
        if (treeResponses != null && treeResponses.size() > 0) {
            Comparator comparator = Collator.getInstance(Locale.CHINA);
            Collections.sort(treeResponses, (p1, p2) -> comparator.compare(
                    PinyinUtils.converterToFirstSpell(p1.getTitle().substring(0, 1)).toLowerCase(),
                    PinyinUtils.converterToFirstSpell(p2.getTitle().substring(0, 1)).toLowerCase()));

        }
        return ResponseResult.success(treeResponses);
    }


    /**
     * 节点名列表
     */
    @UserLoginToken
    @RequestMapping(value = "nodeTree", method = {RequestMethod.POST})
    public ResponseResult<List<StructTreeResponse>> nodeTree() {
        List<StructTreeResponse> treeResponses = iTreeLabelService.nodeTree();
        if (treeResponses != null && treeResponses.size() > 0) {
            Comparator comparator = Collator.getInstance(Locale.CHINA);
            Collections.sort(treeResponses, (p1, p2) -> comparator.compare(
                    PinyinUtils.converterToFirstSpell(p1.getTitle().substring(0, 1)).toLowerCase(),
                    PinyinUtils.converterToFirstSpell(p2.getTitle().substring(0, 1)).toLowerCase()));

        }
        return ResponseResult.success(treeResponses);
    }

    /**
     * 节点名列表
     */
    @UserLoginToken
    @RequestMapping(value = "runNodeTree", method = {RequestMethod.POST})
    public ResponseResult<List<StructTreeResponse>> runNodeTree() {
        List<StructTreeResponse> treeResponses = iTreeLabelService.runNodeTree();
        if (treeResponses != null && treeResponses.size() > 0) {
            Comparator comparator = Collator.getInstance(Locale.CHINA);
            Collections.sort(treeResponses, (p1, p2) -> comparator.compare(
                    PinyinUtils.converterToFirstSpell(p1.getTitle().substring(0, 1)).toLowerCase(),
                    PinyinUtils.converterToFirstSpell(p2.getTitle().substring(0, 1)).toLowerCase()));

        }
        return ResponseResult.success(treeResponses);
    }


}
