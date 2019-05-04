package top.fuyuaaa.shadowpuppets.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.fuyuaaa.shadowpuppets.annotation.ValidateAdmin;
import top.fuyuaaa.shadowpuppets.common.Result;
import top.fuyuaaa.shadowpuppets.common.enums.ExEnum;
import top.fuyuaaa.shadowpuppets.exceptions.ParamException;
import top.fuyuaaa.shadowpuppets.exceptions.UploadException;
import top.fuyuaaa.shadowpuppets.model.PageVO;
import top.fuyuaaa.shadowpuppets.model.bo.GoodsBO;
import top.fuyuaaa.shadowpuppets.model.qo.GoodsListQO;
import top.fuyuaaa.shadowpuppets.model.vo.GoodsVO;
import top.fuyuaaa.shadowpuppets.service.CategoryService;
import top.fuyuaaa.shadowpuppets.service.GoodsService;
import top.fuyuaaa.shadowpuppets.util.FileUtils;
import top.fuyuaaa.shadowpuppets.util.UploadUtil;

import java.io.File;
import java.util.Map;

/**
 * @author: fuyuaaa
 * @creat: 2019-04-29 10:15
 */

@RestController
@RequestMapping("/goods/manager")
@Slf4j
public class GoodsManagerController {

    @Autowired
    GoodsService goodsService;
    @Autowired
    CategoryService categoryService;

    @PostMapping("/list")
    @ValidateAdmin
    public Result<PageVO<GoodsVO>> getManagerGoodsList(@RequestBody GoodsListQO goodsListQO) {
        PageVO<GoodsVO> goodsPageVO = goodsService.getGoodsPageVO(goodsListQO);
        return Result.success(goodsPageVO);
    }


    @PostMapping("/add")
    @ValidateAdmin
    public Result addManagerGoods(@RequestBody GoodsBO goodsBO) {
        if (null == goodsBO) {
            return Result.fail("参数不太对劲哦");
        }
        boolean result = goodsService.addManagerGoods(goodsBO);
        return result ? Result.success() : Result.fail("添加失败");
    }

    @PostMapping("/update")
    @ValidateAdmin
    public Result<Boolean> updateManagerGoods(@RequestBody GoodsBO goodsBO) {
        if (null == goodsBO || null == goodsBO.getId()) {
            throw new ParamException(ExEnum.PARAM_ERROR.getMsg());
        }
        boolean result = goodsService.updateManagerGoods(goodsBO);
        return result ? Result.success() : Result.fail("更新失败");
    }

    @PostMapping("/remove")
    @ValidateAdmin
    public Result<Boolean> updateManagerGoods(@RequestParam Integer goodsId) {
        if (null == goodsId) {
            throw new ParamException(ExEnum.PARAM_ERROR.getMsg());
        }
        boolean result = goodsService.removeManagerGoods(goodsId);
        return result ? Result.success() : Result.fail("删除失败");
    }

    @PostMapping("/image/add")
    @ValidateAdmin
    public Result<String> addGoodsDetailsImage(@RequestParam("file") MultipartFile multipartFile,
                                               @RequestParam("goodsId") Integer goodsId) {
        File file = FileUtils.convertMultipartFile2File(multipartFile);
        //上传图片到阿里云
        String resultUrl = UploadUtil.upload2OSSWithGoodsId(goodsId, file);
        //修改商品的图片列表
        if (!goodsService.addGoodsImage(goodsId, resultUrl)) {
            throw new UploadException(ExEnum.UPLOAD_ERROR.getMsg());
        }
        //删除临时文件
        FileUtils.deleteFile(file);
        return Result.success(resultUrl);
    }

    @PostMapping("/image/remove")
    @ValidateAdmin
    public Result removeGoodsDetailsImage(@RequestParam("imageUrl") String imageUrl,
                                          @RequestParam("goodsId") Integer goodsId) {
        goodsService.removeGoodsImage(goodsId, imageUrl);
        return Result.success();
    }

    @PostMapping("/image/main/upload")
    public Result<String> uploadGoodsMainImage(@RequestParam("file") MultipartFile multipartFile) {
        File file = FileUtils.convertMultipartFile2File(multipartFile);
        String resultUrl = UploadUtil.uploadGoodsMain2OSS(file);
        return Result.success(resultUrl);
    }

    /**
     * @return 分类统计信息
     */
    @PostMapping("/category/statistics")
    @ValidateAdmin
    public Result<Map<String, Integer>> categoryStatisticsInfo() {
        Map<String, Integer> statisticsInfo = goodsService.categoryStatisticsInfo();
        return Result.success(statisticsInfo);
    }
}