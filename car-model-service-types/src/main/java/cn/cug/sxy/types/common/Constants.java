package cn.cug.sxy.types.common;

public class Constants {

    public final static String SPLIT = ",";
    public final static String UNDERLINE = "_";

    public static class RedisKey {

        public static final String CAR_MODEL_BY_ID_KEY = "car_model:car_model_by_id_key_";
        public static final String CAR_MODEL_BY_CODE_KEY = "car_model:car_model_by_code_key_";
        public static final String CAR_MODEL_BY_BRAND_KEY = "car_model:car_model_by_brand_key_";
        public static final String CAR_MODEL_BY_POWER_TYPE_KEY = "car_model:car_model_by_power_type_key_";
        public static final String CAR_MODEL_BY_SERIES_ID_KEY = "car_model:car_model_by_series_id_key_";

        public static final String CAR_SERIES_BY_ID_KEY = "car_series:car_series_by_id_key_";
        public static final String CAR_SERIES_BY_CODE_KEY = "car_series:car_series_by_code_key_";
        public static final String CAR_SERIES_BY_BRAND_KEY = "car_series:car_series_by_brand_key_";

        public static final String SYS_CATEGORY_BY_CODE_KEY = "sys_category:sys_category_by_code_key_";
        public static final String SYS_CATEGORY_ALL_KEY = "sys_category:sys_category_all_key_";

        public static final String SYS_GROUP_BY_CODE_KEY = "sys_group:sys_group_by_code_key_";
        public static final String SYS_GROUP_ALL_KEY = "sys_group:sys_group_all_key_";
        public static final String SYS_GROUP_BY_CATEGORY_ID_KEY = "sys_group:sys_group_category_id_key_";

        public static final String TEMPLATE_STRUCTURE_BY_ID_KEY = "template_structure:template_structure_by_id_key_";
        public static final String TEMPLATE_STRUCTURE_BY_CODE_KEY = "template_structure:template_structure_by_code_key_";
        public static final String TEMPLATE_STRUCTURE_BY_CODE_VERSION_KEY = "template_structure:template_structure_by_code_version_key_";
        public static final String TEMPLATE_STRUCTURE_ALL_KEY = "template_structure:template_structure_all_key_";

        public static final String TEMPLATE_STRUCTURE_NODE_BY_ID_KEY = "template_structure:template_structure_node_by_id_key_";
        public static final String TEMPLATE_STRUCTURE_NODES_BY_TEMPLATE_ID_KEY = "template_structure:template_structure_nodes_by_template_id_key_";
        public static final String TEMPLATE_STRUCTURE_NODES_BY_PARENT_ID_KEY = "template_structure:template_structure_nodes_by_parent_id_key_";

    }

}
