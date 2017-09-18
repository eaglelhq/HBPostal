package com.ksource.hbpostal.bean;

import java.util.List;

public class HBAreaResaultBean {
    /**
     * flag : 0
     * areaList : [{"id":"02bb0b1b38d045988b8b7cebff24c2c0","address_name":"西藏自治区","up_id":null},{"id":"0c0b516f3d96419cae1000330b0ac37e","address_name":"吉林省","up_id":null},{"id":"18ee09f13ebb4405bcd54f0c59657dfd","address_name":"海南省","up_id":null},{"id":"2668e3fd58e84288a9a23cccaa902612","address_name":"新疆维吾尔自治区","up_id":null},{"id":"29e8e67f5f934b349b2c98efffb0ff1f","address_name":"青海省","up_id":null},{"id":"2d10664600c848b09a98de6f9a900f21","address_name":"河北省","up_id":null},{"id":"30cf8351512f4a89be3426db51819590","address_name":"山西省","up_id":null},{"id":"357a8ee152fc475c9417e81435ac2b9b","address_name":"甘肃省","up_id":null},{"id":"358e64699d91402b923a3c6c284716c1","address_name":"安徽省","up_id":null},{"id":"36be2090e8244464b8d8fb825bc01058","address_name":"黑龙江省","up_id":null},{"id":"397b29aa10cc4766a24c2b892f9d14dc","address_name":"山东省","up_id":null},{"id":"5e0a85de071647d78760e83e677e2a95","address_name":"宁夏省","up_id":null},{"id":"6bb9cade8e1442f898843f2a22709f24","address_name":"四川省","up_id":null},{"id":"789520e70eaf4b6dab3416116825c7fe","address_name":"辽宁省","up_id":null},{"id":"7af987276833400d9b27851919013fa8","address_name":"福建省","up_id":null},{"id":"81105603eac64f3ab05bf0295e7d5c6e","address_name":"江苏省","up_id":null},{"id":"90e59f52b60845d081e392f865d700d7","address_name":"贵州省","up_id":null},{"id":"95dfd3142a294c11a41fc84e3fc9eb32","address_name":"湖北省","up_id":null},{"id":"accca8e3696048c2bd0c5bf651cf73cf","address_name":"河南省","up_id":null},{"id":"b4425978b0b64f208418b3ac6a05e845","address_name":"陕西省","up_id":null},{"id":"b5ea458c2349414890ae66acff7c4dc5","address_name":"浙江省","up_id":null},{"id":"bc061f32deeb4844919ea4c14d4b4869","address_name":"直辖市","up_id":null},{"id":"ccc3a4ccb1264c2eae1e1e6956ae3317","address_name":"内蒙古自治区","up_id":null},{"id":"d272f1fa0dd24db9aade3b7bc0ac985d","address_name":"云南省","up_id":null},{"id":"da38471d09c34e288e6674f91dafbed5","address_name":"江西省","up_id":null},{"id":"dedc1d32f92a404dab38b2383f8039fb","address_name":"湖南省","up_id":null},{"id":"e71ddd2f064c4fdba4bd02ebaec4e418","address_name":"广西壮族自治区","up_id":null}]
     * msg : 获取成功
     * success : true
     */

    public String flag;
    public String msg;
    public boolean success;
    /**
     * id : 02bb0b1b38d045988b8b7cebff24c2c0
     * address_name : 西藏自治区
     * up_id : null
     */

    public List<AreaListBean> araList;


    public static class AreaListBean {
        public String ID;
        public String NAME;
        public String LEVEL;

    }
}
