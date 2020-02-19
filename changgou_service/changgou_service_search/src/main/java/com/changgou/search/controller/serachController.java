package com.changgou.search.controller;


import com.changgou.entity.Page;
import com.changgou.pojo.SkuInfo;
import com.changgou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/search")
public class serachController {

    @Autowired
    private SearchService searchService;


    public void handlerSearchMap(Map<String,String> serachMap){
        if (serachMap != null){
            Set<Map.Entry<String, String>> entries = serachMap.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                if(entry.getKey().startsWith("spec_")){
                    serachMap.put(entry.getKey(),entry.getValue().replace("+","%2B"));
                }
            }
        }
    }

    @GetMapping("/list")
    public String searchThymelemf (@RequestParam Map<String,String>  searchMap, Model model){

        handlerSearchMap(searchMap);
        Map<String ,Object> search = searchService.search(searchMap);

        model.addAttribute("searchMap",searchMap);
        model.addAttribute("search",search);

        StringBuilder url = new StringBuilder("/search/list");
        if(searchMap != null && searchMap.size() > 0){
            url.append("?");
            for (String key : searchMap.keySet()) {
                if(!"pageNum".equals(key) && !"sortRule".equals(key) && !"  sortField".equals(key)){
                    url.append(key).append("=").append(searchMap.get(key)).append("&");
                }
            }
            String urlString = url.toString();
            urlString = urlString.substring(0,urlString.length()-1);
            model.addAttribute("url",urlString);
        }else {
            model.addAttribute("url",url);
        }
        Page<SkuInfo> page = new Page(Long.parseLong(String.valueOf(search.get("total"))), Integer.parseInt(String.valueOf(search.get("pageNum"))),
                                        Integer.parseInt(String.valueOf(Page.pageSize)));

        model.addAttribute("page",page);
        return "search";

    }

    @GetMapping("/find")
    public Map search(@RequestParam Map<String,String> searchMap){
            handlerSearchMap(searchMap);
        Map map = searchService.search(searchMap);
        return map;
    }



}
