// Vue 3 + Element Plus 应用
const { createApp, ref, reactive, onMounted, onUnmounted, computed, nextTick } = Vue;
const { ElMessage, ElMessageBox } = ElementPlus;

const app = createApp({
    setup() {
        // 响应式数据
        const activeMenu = ref('dashboard');
        const currentUser = ref('管理员');
        
        // 统计数据
        const stats = reactive({
            totalCategories: 0,
            totalItems: 0,
            totalTemplates: 0,
            totalInstances: 0,
            totalUsages: 0
        });
        
        // 配置类别相关数据
        const categories = ref([]);
        const categoryLoading = ref(false);
        const categorySearch = reactive({
            keyword: ''
        });
        
        // 配置项相关数据
        const configItems = ref([]);
        const itemLoading = ref(false);
        const selectedCategoryId = ref(null);
        const itemSearch = reactive({
            keyword: ''
        });
        
        // 用法相关数据
        const usages = ref([]);
        const usageLoading = ref(false);
        const usageQuery = reactive({
            groupNodeId: '',
            statusFilter: 'ENABLED' // 默认只显示可用的用法
        });

        // 模板相关数据
        const templates = ref([]);
        const templateLoading = ref(false);
        const templateQuery = reactive({
            templateCode: '',
            nameKeyword: '',
            status: ''
        });

        // 实例相关数据
        const instances = ref([]);
        const instanceLoading = ref(false);
        const instanceQuery = reactive({
            instanceCode: '',
            nameKeyword: '',
            status: ''
        });

        // 可用的模板和实例列表（用于下拉选择）
        const availableTemplates = ref([]);
        const availableInstances = ref([]);

        // 用法对话框
        const usageDialog = reactive({
            visible: false,
            isEdit: false,
            loading: false,
            form: {
                usageId: null,
                usageName: '',
                instanceId: null,
                parentGroupNodeId: null,
                groupId: null,
                sortOrder: 1,
                explodedViewFile: null,
                creator: '管理员',
                combinations: []
            },
            rules: {
                usageName: [
                    { required: true, message: '请输入用法名称', trigger: 'blur' }
                ],
                instanceId: [
                    { required: true, message: '请输入实例ID', trigger: 'blur' }
                ],
                parentGroupNodeId: [
                    { required: true, message: '请输入父组节点ID', trigger: 'blur' }
                ],
                groupId: [
                    { required: true, message: '请输入系统分组ID', trigger: 'blur' }
                ],
                creator: [
                    { required: true, message: '请输入创建人', trigger: 'blur' }
                ]
            }
        });

        // 用法详情对话框
        const usageDetailDialog = reactive({
            visible: false,
            usageId: null,
            usageName: '',
            status: '',
            createdTime: '',
            updatedTime: '',
            combinations: [],
            explodedViewImg: '',
            relatedParts: [],
            activeTab: 'basic'
        });
        
        // 关联备件加载状态
        const relatedPartsLoading = ref(false);
        
        // 配置组合加载状态
        const combinationsLoading = ref(false);

        // 图片查看对话框
        const imageDialog = reactive({
            visible: false,
            imageUrl: ''
        });
        const systemGroups = ref([]);
        const allConfigItems = ref([]);

        // 模板对话框
        const templateDialog = reactive({
            visible: false,
            isEdit: false,
            loading: false,
            form: {
                templateId: null,
                templateCode: '',
                templateName: '',
                templateDesc: '',
                version: '',
                creator: '管理员',
                nodes: []
            },
            rules: {
                templateCode: [
                    { required: true, message: '请输入模板编码', trigger: 'blur' }
                ],
                templateName: [
                    { required: true, message: '请输入模板名称', trigger: 'blur' }
                ],
                version: [
                    { required: true, message: '请输入版本号', trigger: 'blur' }
                ],
                creator: [
                    { required: true, message: '请输入创建人', trigger: 'blur' }
                ]
            }
        });

        // 模板节点对话框
        const templateNodeDialog = reactive({
            visible: false,
            isEdit: false,
            loading: false,
            parentNode: null,
            form: {
                tempId: null,
                nodeName: '',
                nodeType: 'CATEGORY',
                sortOrder: 1,
                nodeDesc: ''
            },
            rules: {
                nodeName: [
                    { required: true, message: '请输入节点名称', trigger: 'blur' }
                ],
                nodeType: [
                    { required: true, message: '请选择节点类型', trigger: 'change' }
                ],
                sortOrder: [
                    { required: true, message: '请输入排序序号', trigger: 'blur' }
                ]
            }
        });

        // 模板详情对话框
        const templateDetailDialog = reactive({
            visible: false,
            data: null,
            templateName: ''
        });

        // 实例对话框
        const instanceDialog = reactive({
            visible: false,
            isEdit: false,
            loading: false,
            form: {
                instanceId: null,
                templateId: null,
                instanceCode: '',
                instanceName: '',
                instanceDesc: '',
                seriesId: null,
                modelId: null,
                instanceVersion: '',
                creator: '管理员'
            },
            rules: {
                templateId: [
                    { required: true, message: '请选择模板', trigger: 'change' }
                ],
                instanceCode: [
                    { required: true, message: '请输入实例编码', trigger: 'blur' }
                ],
                instanceName: [
                    { required: true, message: '请输入实例名称', trigger: 'blur' }
                ],
                instanceVersion: [
                    { required: true, message: '请输入版本号', trigger: 'blur' }
                ],
                creator: [
                    { required: true, message: '请输入创建人', trigger: 'blur' }
                ]
            }
        });

        // 实例详情对话框
        const instanceDetailDialog = reactive({
            visible: false,
            data: null,
            instanceName: ''
        });

        // 实例对比对话框
        const instanceCompareDialog = reactive({
            visible: false,
            instance1Id: null,
            instance2Id: null,
            compareData: null,
            viewMode: 'side-by-side', // 'side-by-side' 或 'unified'
            showOnlyDifferences: false,
            ignoreWhitespace: false,
            diffStats: {
                added: 0,
                removed: 0,
                modified: 0
            }
        });

        // 右键菜单状态
        const contextMenu = reactive({
            visible: false,
            x: 0,
            y: 0,
            type: '', // 'template' 或 'instance'
            currentNode: null,
            currentData: null,
            currentTreeNode: null
        });

        // 节点添加对话框
        const nodeDialog = reactive({
            visible: false,
            loading: false,
            isEdit: false,
            type: '', // 'template' 或 'instance'
            parentData: null,
            form: {
                nodeName: '',
                nodeNameEn: '',
                nodeType: '',
                id: null, // 统一的ID字段，根据节点类型映射到categoryId/groupId/usageId
                sortOrder: 1
            },
            rules: {
                nodeName: [
                    { required: true, message: '请输入节点名称', trigger: 'blur' }
                ],
                nodeType: [
                    { required: true, message: '请选择节点类型', trigger: 'change' }
                ],
                id: [
                    { required: true, message: '请输入ID', trigger: 'blur' }
                ]
            }
        });
        
        // 对话框数据
        const categoryDialog = reactive({
            visible: false,
            isEdit: false,
            loading: false,
            form: {
                categoryCode: '',
                categoryName: '',
                sortOrder: 1,
                creator: '管理员'
            },
            rules: {
                categoryCode: [
                    { required: true, message: '请输入类别编码', trigger: 'blur' }
                ],
                categoryName: [
                    { required: true, message: '请输入类别名称', trigger: 'blur' }
                ],
                sortOrder: [
                    { required: true, message: '请输入排序序号', trigger: 'blur' }
                ],
                creator: [
                    { required: true, message: '请输入创建人', trigger: 'blur' }
                ]
            }
        });
        
        const itemDialog = reactive({
            visible: false,
            isEdit: false,
            loading: false,
            form: {
                categoryId: null,
                itemCode: '',
                itemName: '',
                itemValue: '',
                creator: '管理员'
            },
            rules: {
                categoryId: [
                    { required: true, message: '请选择配置类别', trigger: 'change' }
                ],
                itemCode: [
                    { required: true, message: '请输入配置编码', trigger: 'blur' }
                ],
                itemName: [
                    { required: true, message: '请输入配置名称', trigger: 'blur' }
                ],
                itemValue: [
                    { required: true, message: '请输入配置值', trigger: 'blur' }
                ],
                creator: [
                    { required: true, message: '请输入创建人', trigger: 'blur' }
                ]
            }
        });

        // 树形结构配置
        const templateTreeProps = {
            children: 'children',
            label: 'nodeName'
        };

        const instanceTreeProps = {
            children: 'children',
            label: 'nodeName'
        };

        // 节点类型工具方法
        const getNodeTypeText = (nodeType) => {
            const typeMap = {
                'CATEGORY': '系统大类',
                'GROUP': '系统分组',
                'USAGE': '用法'
            };
            return typeMap[nodeType] || nodeType;
        };

        const getNodeTypeColor = (nodeType) => {
            const colorMap = {
                'CATEGORY': 'primary',
                'GROUP': 'success',
                'USAGE': 'warning'
            };
            return colorMap[nodeType] || 'info';
        };

        // 格式化时间
        const formatDateTime = (dateTime) => {
            if (!dateTime) return '-';
            return new Date(dateTime).toLocaleString('zh-CN');
        };

        // 格式化状态
        const formatStatus = (status) => {
            const statusMap = {
                'ENABLED': '启用',
                'DISABLED': '禁用'
            };
            return statusMap[status] || status;
        };

        // 获取ID字段的占位符文本
        const getIdPlaceholder = (nodeType) => {
            const placeholderMap = {
                'CATEGORY': '请输入系统大类ID',
                'GROUP': '请输入系统分组ID',
                'USAGE': '请输入用法ID'
            };
            return placeholderMap[nodeType] || '请输入ID';
        };

        // API 基础配置
        const API_BASE = 'http://localhost:8091';

        // HTTP 请求封装
        const request = async (url, options = {}) => {
            try {
                console.log('API Request:', `${API_BASE}${url}`, options);

                // 构建请求配置
                const fetchOptions = { ...options };

                // 如果是FormData，不设置Content-Type，让浏览器自动设置
                if (!options.isFormData) {
                    fetchOptions.headers = {
                        'Content-Type': 'application/json',
                        ...options.headers
                    };
                } else {
                    fetchOptions.headers = {
                        ...options.headers
                    };
                    delete fetchOptions.isFormData; // 删除自定义标记
                }

                const response = await fetch(`${API_BASE}${url}`, fetchOptions);

                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }

                const data = await response.json();
                console.log('API Response:', data);

                // 检查响应码，支持字符串和数字类型
                const code = String(data.code);
                if (code !== '0000') {
                    console.error('API业务错误:', {
                        code: data.code,
                        info: data.info,
                        message: data.message
                    });
                    throw new Error(data.info || data.message || `请求失败，错误码: ${data.code}`);
                }

                console.log('API请求成功:', data.data);
                return data.data;
            } catch (error) {
                console.error('Request failed:', error);
                ElMessage.error(error.message || '网络请求失败');
                throw error;
            }
        };
        
        // 菜单切换
        const handleMenuSelect = (key) => {
            activeMenu.value = key;

            // 根据菜单加载对应数据
            switch (key) {
                case 'dashboard':
                    loadDashboardData();
                    break;
                case 'config-category':
                    loadCategories();
                    break;
                case 'config-item':
                    loadCategories();
                    break;
                case 'template':
                    queryTemplates();
                    break;
                case 'instance':
                    queryInstances();
                    break;
                case 'usage':
                    // 用法管理页面，无需额外加载数据
                    console.log('切换到用法管理页面');
                    break;
                case 'part':
                    // 备件管理页面，加载备件列表
                    console.log('切换到备件管理页面');
                    refreshParts();
                    break;
                case 'workhour':
                    // 工时管理页面，加载工时列表
                    console.log('切换到工时管理页面');
                    refreshWorkHours();
                    break;
            }
        };
        
        // 加载仪表盘数据
        const loadDashboardData = async () => {
            try {
                // 并行加载各模块的统计数据
                const [categoriesData, templatesData, instancesData] = await Promise.allSettled([
                    // 加载配置类别数据
                    request('/api/v1/config/get_categories'),
                    // 加载结构模板数据
                    request('/api/v1/structure/template/query_templates', {
                        method: 'POST',
                        body: JSON.stringify({
                            templateCode: null,
                            status: null,
                            nameKeyword: null,
                            pageNo: 1,
                            pageSize: 1000
                        })
                    }),
                    // 加载结构实例数据
                    request('/api/v1/structure/instance/query_instances', {
                        method: 'POST',
                        body: JSON.stringify({
                            instanceCode: null,
                            nameKeyword: null,
                            status: null,
                            seriesId: null,
                            modelId: null,
                            pageNo: 1,
                            pageSize: 1000
                        })
                    })
                ]);

                console.log('API responses:', { categoriesData, templatesData, instancesData });

                // 统计配置类别数量
                if (categoriesData.status === 'fulfilled') {
                    const categories = categoriesData.value;
                    console.log('Categories data:', categories);
                    stats.totalCategories = Array.isArray(categories) ? categories.length : 0;

                    // 统计配置项数量（需要遍历每个类别）
                    let totalItems = 0;
                    if (Array.isArray(categories)) {
                        for (const category of categories) {
                            try {
                                const items = await request(`/api/v1/config/get_config_items?categoryId=${category.id}`);
                                console.log(`Items for category ${category.id}:`, items);
                                totalItems += Array.isArray(items) ? items.length : 0;
                            } catch (error) {
                                console.warn(`Failed to load items for category ${category.id}:`, error);
                            }
                        }
                    }
                    stats.totalItems = totalItems;
                } else {
                    console.error('Failed to load categories:', categoriesData.reason);
                    stats.totalCategories = 0;
                    stats.totalItems = 0;
                }

                // 统计结构模板数量
                if (templatesData.status === 'fulfilled') {
                    const templates = templatesData.value;
                    console.log('Templates data:', templates);
                    // 优先使用total字段，这是分页数据的总数
                    if (templates && typeof templates.total === 'number') {
                        stats.totalTemplates = templates.total;
                    } else if (Array.isArray(templates)) {
                        stats.totalTemplates = templates.length;
                    } else {
                        stats.totalTemplates = 0;
                    }
                } else {
                    console.error('Failed to load templates:', templatesData.reason);
                    stats.totalTemplates = 0;
                }

                // 统计结构实例数量
                if (instancesData.status === 'fulfilled') {
                    const instances = instancesData.value;
                    console.log('Instances data:', instances);
                    // 优先使用total字段，这是分页数据的总数
                    if (instances && typeof instances.total === 'number') {
                        stats.totalInstances = instances.total;
                    } else if (Array.isArray(instances)) {
                        stats.totalInstances = instances.length;
                    } else {
                        stats.totalInstances = 0;
                    }
                } else {
                    console.error('Failed to load instances:', instancesData.reason);
                    stats.totalInstances = 0;
                }

                // 用法数量暂时设为0，因为没有对应的查询接口
                stats.totalUsages = 0;

                console.log('Dashboard stats loaded:', stats);
            } catch (error) {
                console.error('Failed to load dashboard data:', error);
                // 设置默认值
                stats.totalCategories = 0;
                stats.totalItems = 0;
                stats.totalTemplates = 0;
                stats.totalInstances = 0;
                stats.totalUsages = 0;
            }
        };
        
        // 加载配置类别
        const loadCategories = async () => {
            categoryLoading.value = true;
            try {
                if (categorySearch.keyword && categorySearch.keyword.trim()) {
                    // 有搜索关键词时使用搜索接口
                    console.log('使用搜索接口查询类别:', categorySearch.keyword.trim());
                    const data = await request('/api/v1/config/search_categories', {
                        method: 'POST',
                        body: JSON.stringify({
                            categoryCode: null,
                            nameKeyword: categorySearch.keyword.trim()
                        })
                    });
                    categories.value = data || [];
                } else {
                    // 无搜索关键词时查询所有类别
                    console.log('查询所有配置类别');
                    const data = await request('/api/v1/config/get_categories');
                    categories.value = data || [];
                }
            } catch (error) {
                categories.value = [];
            } finally {
                categoryLoading.value = false;
            }
        };
        
        // 搜索配置类别
        const searchCategories = () => {
            console.log('搜索配置类别，关键词:', categorySearch.keyword);
            // 重新加载类别（会根据关键词自动选择接口）
            loadCategories();
        };

        // 清空搜索条件
        const clearSearch = () => {
            categorySearch.keyword = '';
            console.log('清空搜索条件，重新加载所有类别');
            loadCategories();
        };
        
        // 刷新配置类别
        const refreshCategories = () => {
            loadCategories();
        };
        
        // 显示创建类别对话框
        const showCreateCategoryDialog = () => {
            categoryDialog.visible = true;
            categoryDialog.isEdit = false;
            Object.assign(categoryDialog.form, {
                categoryCode: '',
                categoryName: '',
                sortOrder: 1,
                creator: '管理员'
            });
        };
        
        // 编辑配置类别
        const editCategory = (row) => {
            categoryDialog.visible = true;
            categoryDialog.isEdit = true;

            // 重置表单验证状态
            if (categoryFormRef.value) {
                categoryFormRef.value.clearValidate();
            }

            // 填充表单数据
            Object.assign(categoryDialog.form, {
                id: row.id,
                categoryCode: row.categoryCode,
                categoryName: row.categoryName,
                sortOrder: row.sortOrder
            });

            console.log('编辑配置类别:', categoryDialog.form);
        };
        
        // 创建表单引用
        const categoryFormRef = ref(null);
        const itemFormRef = ref(null);
        const usageFormRef = ref(null);

        // 提交配置类别
        const submitCategory = async () => {
            // 表单验证
            if (!categoryFormRef.value) {
                console.error('表单引用未找到');
                return;
            }

            try {
                // 使用Element Plus的表单验证
                await categoryFormRef.value.validate();
            } catch (error) {
                console.log('表单验证失败:', error);
                return;
            }

            categoryDialog.loading = true;
            console.log('开始提交配置类别:', categoryDialog.form);

            try {
                if (categoryDialog.isEdit) {
                    await request(`/api/v1/config/update_category`, {
                        method: 'POST',
                        body: JSON.stringify({
                            categoryId: categoryDialog.form.id,
                            categoryCode: categoryDialog.form.categoryCode,
                            categoryName: categoryDialog.form.categoryName,
                            sortOrder: categoryDialog.form.sortOrder
                        })
                    });
                    ElMessage.success('更新成功');
                } else {
                    await request('/api/v1/config/create_category', {
                        method: 'POST',
                        body: JSON.stringify({
                            categoryCode: categoryDialog.form.categoryCode,
                            categoryName: categoryDialog.form.categoryName,
                            sortOrder: categoryDialog.form.sortOrder,
                            creator: categoryDialog.form.creator
                        })
                    });
                    ElMessage.success('创建成功');
                }

                // 关闭对话框
                categoryDialog.visible = false;

                // 清空搜索关键词，确保显示所有数据
                categorySearch.keyword = '';

                // 刷新数据列表
                await loadCategories();
            } catch (error) {
                console.error('提交配置类别失败:', error);
                ElMessage.error('操作失败: ' + (error.message || '未知错误'));
            } finally {
                categoryDialog.loading = false;
            }
        };
        
        // 删除配置类别
        const deleteCategory = (row) => {
            console.log('准备删除配置类别:', row);

            ElMessageBox.confirm(
                `确定要删除配置类别"${row.categoryName}"吗？`,
                '确认删除',
                {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning',
                    dangerouslyUseHTMLString: false
                }
            ).then(async () => {
                try {
                    console.log('开始删除配置类别:', row.id);

                    await request(`/api/v1/config/delete_category?categoryId=${row.id}`, {
                        method: 'POST'
                    });

                    ElMessage.success('删除成功');

                    // 清空搜索条件并刷新数据
                    categorySearch.keyword = '';
                    await loadCategories();

                } catch (error) {
                    console.error('删除配置类别失败:', error);
                    ElMessage.error('删除失败: ' + (error.message || '未知错误'));
                }
            }).catch(() => {
                console.log('用户取消删除');
            });
        };
        
        // 选择配置类别
        const selectCategory = async (categoryId) => {
            selectedCategoryId.value = categoryId;
            await loadConfigItems(categoryId);
        };
        
        // 加载配置项
        const loadConfigItems = async (categoryId) => {
            if (!categoryId) return;

            itemLoading.value = true;
            try {
                const data = await request(`/api/v1/config/get_config_items?categoryId=${categoryId}`);
                configItems.value = data || [];
            } catch (error) {
                configItems.value = [];
            } finally {
                itemLoading.value = false;
            }
        };
        
        // 显示创建配置项对话框
        const showCreateItemDialog = () => {
            itemDialog.visible = true;
            itemDialog.isEdit = false;
            Object.assign(itemDialog.form, {
                categoryId: selectedCategoryId.value,
                itemCode: '',
                itemName: '',
                itemValue: '',
                creator: '管理员'
            });
        };

        // 编辑配置项
        const editItem = (row) => {
            itemDialog.visible = true;
            itemDialog.isEdit = true;

            // 重置表单验证状态
            if (itemFormRef.value) {
                itemFormRef.value.clearValidate();
            }

            // 填充表单数据
            Object.assign(itemDialog.form, {
                id: row.id,
                categoryId: row.categoryId,
                itemCode: row.itemCode,
                itemName: row.itemName,
                itemValue: row.itemValue
            });

            console.log('编辑配置项:', itemDialog.form);
        };

        // 提交配置项
        const submitItem = async () => {
            // 表单验证
            if (!itemFormRef.value) {
                console.error('配置项表单引用未找到');
                return;
            }

            try {
                // 使用Element Plus的表单验证
                await itemFormRef.value.validate();
            } catch (error) {
                console.log('配置项表单验证失败:', error);
                return;
            }

            itemDialog.loading = true;
            console.log('开始提交配置项:', itemDialog.form);

            try {
                if (itemDialog.isEdit) {
                    // 更新配置项功能需要后端添加对应的HTTP接口
                    ElMessage.warning('更新配置项功能需要后端支持，请联系开发人员添加 update_config_item 接口');
                    return;
                } else {
                    await request('/api/v1/config/create_config_item', {
                        method: 'POST',
                        body: JSON.stringify({
                            categoryId: itemDialog.form.categoryId,
                            itemCode: itemDialog.form.itemCode,
                            itemName: itemDialog.form.itemName,
                            itemValue: itemDialog.form.itemValue,
                            creator: itemDialog.form.creator
                        })
                    });
                    ElMessage.success('创建成功');
                }

                // 关闭对话框
                itemDialog.visible = false;

                // 清空搜索关键词
                itemSearch.keyword = '';

                // 刷新配置项列表
                await loadConfigItems(selectedCategoryId.value);
            } catch (error) {
                console.error('提交配置项失败:', error);
                ElMessage.error('操作失败: ' + (error.message || '未知错误'));
            } finally {
                itemDialog.loading = false;
            }
        };

        // 删除配置项
        const deleteItem = (row) => {
            console.log('准备删除配置项:', row);

            ElMessageBox.confirm(
                `确定要删除配置项"${row.itemName}"吗？`,
                '确认删除',
                {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning',
                    dangerouslyUseHTMLString: false
                }
            ).then(async () => {
                try {
                    console.log('开始删除配置项:', row.id);

                    await request(`/api/v1/config/delete_config_item?itemId=${row.id}`, {
                        method: 'POST'
                    });

                    ElMessage.success('删除成功');
                    console.log('删除成功，刷新配置项列表');

                    // 清空搜索条件并刷新配置项列表
                    itemSearch.keyword = '';
                    await loadConfigItems(selectedCategoryId.value);

                } catch (error) {
                    console.error('删除配置项失败:', error);
                    ElMessage.error('删除失败: ' + (error.message || '未知错误'));
                }
            }).catch(() => {
                console.log('用户取消删除');
            });
        };

        // 搜索配置项
        const searchItems = () => {
            console.log('搜索配置项，关键词:', itemSearch.keyword);
            if (itemSearch.keyword && itemSearch.keyword.trim()) {
                // 使用搜索接口
                loadConfigItemsByKeyword(itemSearch.keyword.trim());
            } else {
                // 重新加载当前类别的配置项
                loadConfigItems(selectedCategoryId.value);
            }
        };

        // 清空配置项搜索
        const clearItemSearch = () => {
            itemSearch.keyword = '';
            console.log('清空配置项搜索条件，重新加载当前类别配置项');
            loadConfigItems(selectedCategoryId.value);
        };

        // 根据关键词搜索配置项
        const loadConfigItemsByKeyword = async (keyword) => {
            itemLoading.value = true;
            try {
                const data = await request(`/api/v1/config/search_config_items?keyword=${encodeURIComponent(keyword)}`);
                configItems.value = data || [];
            } catch (error) {
                configItems.value = [];
            } finally {
                itemLoading.value = false;
            }
        };

        // 退出登录
        const logout = () => {
            ElMessageBox.confirm('确定要退出登录吗？', '确认退出', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning',
            }).then(() => {
                ElMessage.success('已退出登录');
                // 这里可以添加退出登录的逻辑
            });
        };
        
        // ==================== 用法管理方法 ====================

        // 查询用法列表
        const queryUsages = async () => {
            if (!usageQuery.groupNodeId) {
                ElMessage.warning('请输入组节点ID');
                return;
            }

            usageLoading.value = true;
            try {
                console.log('查询用法列表，组节点ID:', usageQuery.groupNodeId, '状态过滤:', usageQuery.statusFilter);

                // 根据状态过滤选择不同的API
                let apiUrl;
                if (usageQuery.statusFilter === 'ALL') {
                    // 查询所有状态的用法
                    apiUrl = `/ap1/v1/usage/query_all_usages?groupNodeId=${usageQuery.groupNodeId}`;
                } else {
                    // 查询启用状态的用法（默认）
                    apiUrl = `/ap1/v1/usage/query_usages?groupNodeId=${usageQuery.groupNodeId}`;
                }

                const data = await request(apiUrl, {
                    method: 'GET'
                });

                // 如果是前端过滤已删除状态
                if (usageQuery.statusFilter === 'DISABLED') {
                    usages.value = (data || []).filter(usage => usage.status === 'DISABLED');
                } else {
                    usages.value = data || [];
                }
            } catch (error) {
                console.error('查询用法列表失败:', error);
                ElMessage.error('查询失败: ' + (error.message || '未知错误'));
                usages.value = [];
            } finally {
                usageLoading.value = false;
            }
        };

        // 清空查询条件
        const clearUsageQuery = () => {
            usageQuery.groupNodeId = '';
            usageQuery.statusFilter = 'ENABLED'; // 重置为默认状态
            usages.value = [];
            console.log('清空用法查询条件');
        };

        // 显示创建用法对话框
        const showCreateUsageDialog = async () => {
            usageDialog.visible = true;
            usageDialog.isEdit = false;
            Object.assign(usageDialog.form, {
                usageId: null,
                usageName: '',
                instanceId: '',
                parentGroupNodeId: '',
                groupId: '',
                sortOrder: 1,
                explodedViewFile: null,
                creator: '管理员',
                combinations: []
            });

            // 确保配置类别列表已加载
            if (categories.value.length === 0) {
                await loadCategories();
            }

            console.log('显示创建用法对话框，可用类别数量:', categories.value.length);
        };

        // 编辑用法
        const editUsage = (row) => {
            usageDialog.visible = true;
            usageDialog.isEdit = true;
            Object.assign(usageDialog.form, {
                usageId: row.id,
                usageName: row.usageName,
                groupId: null, // UsageBaseVO中没有groupId，需要用户重新输入
                explodedViewFile: null
            });
            console.log('编辑用法:', usageDialog.form);
        };

        // 提交用法
        const submitUsage = async () => {
            // 数据验证
            if (!usageDialog.form.usageName) {
                ElMessage.error('请输入用法名称');
                return;
            }

            if (!usageDialog.isEdit) {
                if (!usageDialog.form.instanceId) {
                    ElMessage.error('请输入实例ID');
                    return;
                }
                if (!usageDialog.form.parentGroupNodeId) {
                    ElMessage.error('请输入父组节点ID');
                    return;
                }
                if (!usageDialog.form.groupId) {
                    ElMessage.error('请输入系统分组ID');
                    return;
                }
                if (!usageDialog.form.creator) {
                    ElMessage.error('请输入创建人');
                    return;
                }
            }

            // 验证配置组合
            if (usageDialog.form.combinations && usageDialog.form.combinations.length > 0) {
                for (let i = 0; i < usageDialog.form.combinations.length; i++) {
                    const combination = usageDialog.form.combinations[i];
                    if (!combination.combinationName) {
                        ElMessage.error(`请输入第${i + 1}个配置组合的名称`);
                        return;
                    }
                    if (!combination.configItemIds || combination.configItemIds.length === 0) {
                        ElMessage.error(`第${i + 1}个配置组合必须选择至少一个配置项`);
                        return;
                    }
                }
            }

            usageDialog.loading = true;
            console.log('开始提交用法:', usageDialog.form);

            try {
                // 创建FormData对象用于文件上传
                const formData = new FormData();


                formData.append('usageName', usageDialog.form.usageName);

                // 确保groupId是数字或null
                if (usageDialog.form.groupId && usageDialog.form.groupId !== 'null') {
                    formData.append('groupId', usageDialog.form.groupId);
                }

                if (usageDialog.isEdit) {
                    formData.append('usageId', usageDialog.form.usageId);
                    if (usageDialog.form.explodedViewFile) {
                        formData.append('explodedViewFile', usageDialog.form.explodedViewFile);
                    }

                    // 添加配置组合数据 - 使用数组形式
                    if (usageDialog.form.combinations && usageDialog.form.combinations.length > 0) {
                        usageDialog.form.combinations.forEach((combination, index) => {
                            formData.append(`combinations[${index}].combinationName`, combination.combinationName);
                            formData.append(`combinations[${index}].sortOrder`, combination.sortOrder);

                            // 添加配置项ID列表
                            if (combination.configItemIds && combination.configItemIds.length > 0) {
                                combination.configItemIds.forEach((itemId, itemIndex) => {
                                    formData.append(`combinations[${index}].configItemIds[${itemIndex}]`, itemId);
                                });
                            }
                        });
                    }

                    await request('/ap1/v1/usage/update_usage', {
                        method: 'POST',
                        body: formData,
                        isFormData: true
                    });
                    ElMessage.success('更新成功');
                } else {
                    // 确保必填字段不为null
                    if (usageDialog.form.instanceId && usageDialog.form.instanceId !== 'null') {
                        formData.append('instanceId', usageDialog.form.instanceId);
                    }
                    if (usageDialog.form.parentGroupNodeId && usageDialog.form.parentGroupNodeId !== 'null') {
                        formData.append('parentGroupNodeId', usageDialog.form.parentGroupNodeId);
                    }
                    formData.append('sortOrder', usageDialog.form.sortOrder || 1);
                    formData.append('creator', usageDialog.form.creator);

                    if (usageDialog.form.explodedViewFile) {
                        formData.append('explodedViewFile', usageDialog.form.explodedViewFile);
                    }

                    // 添加配置组合数据 - 使用数组形式
                    if (usageDialog.form.combinations && usageDialog.form.combinations.length > 0) {
                        console.log('添加配置组合数据，数量:', usageDialog.form.combinations.length);
                        usageDialog.form.combinations.forEach((combination, index) => {
                            console.log(`配置组合[${index}]:`, combination.combinationName, '配置项数量:', combination.configItemIds?.length || 0);
                            formData.append(`combinations[${index}].combinationName`, combination.combinationName);
                            formData.append(`combinations[${index}].sortOrder`, combination.sortOrder);

                            // 添加配置项ID列表
                            if (combination.configItemIds && combination.configItemIds.length > 0) {
                                combination.configItemIds.forEach((itemId, itemIndex) => {
                                    console.log(`  配置项[${itemIndex}]:`, itemId);
                                    formData.append(`combinations[${index}].configItemIds[${itemIndex}]`, itemId);
                                });
                            }
                        });
                    } else {
                        console.log('没有配置组合数据');
                    }

                    // 打印FormData的所有键值对（用于调试）
                    console.log('FormData内容:');
                    for (let [key, value] of formData.entries()) {
                        console.log(`  ${key}:`, value);
                    }

                    console.log('准备发送创建用法请求...');
                    const result = await request('/ap1/v1/usage/create_usage', {
                        method: 'POST',
                        body: formData,
                        isFormData: true
                    });
                    console.log('创建用法请求成功，返回结果:', result);
                    ElMessage.success('创建成功');
                }

                // 关闭对话框
                usageDialog.visible = false;

                // 刷新列表
                if (usageQuery.groupNodeId) {
                    await queryUsages();
                }

                console.log('用法操作完成');
            } catch (error) {
                console.error('提交用法失败:', error);
                ElMessage.error('操作失败: ' + (error.message || '未知错误'));
            } finally {
                usageDialog.loading = false;
            }
        };

        // 查看用法详情
        const viewUsageDetail = async (row) => {
            try {
                console.log('查看用法详情:', row.id, row.usageName);
                const data = await request(`/ap1/v1/usage/get_usage_detail?usageId=${row.id}`);
                console.log('获取用法详情数据:', data);

                usageDetailDialog.usageId = row.id;
                usageDetailDialog.usageName = row.usageName;
                usageDetailDialog.status = row.status;
                usageDetailDialog.createdTime = row.createdTime;
                usageDetailDialog.updatedTime = row.updatedTime;
                usageDetailDialog.explodedViewImg = row.explodedViewImg;
                usageDetailDialog.combinations = data?.combinations || [];
                usageDetailDialog.activeTab = 'basic';
                usageDetailDialog.visible = true;
                
                // 加载关联的备件
                loadRelatedParts(row.id);

                console.log('获取用法详情成功，配置组合数量:', data?.combinations?.length || 0);
            } catch (error) {
                console.error('获取用法详情失败:', error);
                ElMessage.error('获取详情失败: ' + (error.message || '未知错误'));
            }
        };
        
        // 加载用法关联的备件
        const loadRelatedParts = async (usageId) => {
            if (!usageId) return;
            
            try {
                relatedPartsLoading.value = true;
                const response = await request(`/api/v1/usage_part/list?usageId=${usageId}`);
                usageDetailDialog.relatedParts = response || [];
                console.log('已加载关联备件:', usageDetailDialog.relatedParts);
            } catch (error) {
                console.error('加载关联备件失败:', error);
                ElMessage.error('加载关联备件失败: ' + error.message);
            } finally {
                relatedPartsLoading.value = false;
            }
        };
        
        // 搜索关联备件
        const searchRelatedParts = () => {
            if (!usageDetailDialog.relatedParts) return;
            
            const keyword = partSearch.keyword?.toLowerCase();
            if (!keyword) {
                // 如果没有关键词，重新加载所有备件
                loadRelatedParts(usageDetailDialog.usageId);
                return;
            }
            
            // 本地过滤
            const filteredParts = usageDetailDialog.relatedParts.filter(part => {
                return part.partCode?.toLowerCase().includes(keyword) || 
                       part.partName?.toLowerCase().includes(keyword);
            });
            
            usageDetailDialog.relatedParts = filteredParts;
        };
        
        // 解绑备件
        const unbindPart = async (part) => {
            try {
                await ElMessageBox.confirm(`确定要解绑备件 ${part.partCode} - ${part.partName} 吗?`, '确认解绑', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                });
                
                // 创建请求体
                const requestBody = {
                    usageId: usageDetailDialog.usageId,
                    partId: part.partId
                };
                
                // 发送POST请求
                await request('/api/v1/usage_part/unbind', {
                    method: 'POST',
                    body: JSON.stringify(requestBody)
                });
                
                ElMessage.success('解绑成功');
                
                // 重新加载关联备件
                loadRelatedParts(usageDetailDialog.usageId);
            } catch (error) {
                if (error !== 'cancel') {
                    console.error('解绑备件失败:', error);
                    ElMessage.error('解绑备件失败: ' + error.message);
                }
            }
        };
        
        // 清空所有备件
        const clearAllParts = async () => {
            try {
                await ElMessageBox.confirm('确定要清空所有关联的备件吗?', '确认清空', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                });
                
                const params = {
                    usageId: usageDetailDialog.usageId
                };
                
                await request('/api/v1/usage_part/clear', {
                    method: 'POST',
                    body: JSON.stringify(params)
                });
                
                ElMessage.success('清空关联备件成功');
                
                // 重新加载关联备件
                loadRelatedParts(usageDetailDialog.usageId);
            } catch (error) {
                if (error !== 'cancel') {
                    console.error('清空关联备件失败:', error);
                    ElMessage.error('清空关联备件失败: ' + error.message);
                }
            }
        };
        
        // 下载备件模板
        const downloadPartTemplate = async () => {
            try {
                // 使用原生fetch下载文件
                const response = await fetch(`${API_BASE}/api/v1/usage_part/download_template`, {
                    method: 'GET'
                });
                
                if (!response.ok) {
                    throw new Error(`下载失败: ${response.status}`);
                }
                
                // 获取文件blob
                const blob = await response.blob();
                
                // 创建下载链接
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.style.display = 'none';
                a.href = url;
                a.download = '用法备件关联模板.xlsx';
                document.body.appendChild(a);
                a.click();
                window.URL.revokeObjectURL(url);
                document.body.removeChild(a);
                
                ElMessage.success('模板下载成功');
            } catch (error) {
                console.error('下载模板失败:', error);
                ElMessage.error('下载模板失败: ' + error.message);
            }
        };
        
        // 上传文件前的验证
        const beforePartUpload = (file) => {
            // 验证文件类型
            const isExcel = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' || 
                            file.type === 'application/vnd.ms-excel';
            if (!isExcel) {
                ElMessage.error('只能上传Excel文件!');
                return false;
            }
            
            // 验证文件大小 (5MB)
            const isLt5M = file.size / 1024 / 1024 < 5;
            if (!isLt5M) {
                ElMessage.error('文件大小不能超过5MB!');
                return false;
            }
            
            return true;
        };
        
        // 上传备件关联
        const uploadPartRelations = async (options) => {
            try {
                const { file } = options;
                
                // 创建FormData
                const formData = new FormData();
                formData.append('file', file);
                formData.append('usageId', usageDetailDialog.usageId);
                
                // 发送请求
                const response = await request('/api/v1/usage_part/batch_upload', {
                    method: 'POST',
                    body: formData,
                    isFormData: true
                });
                
                // 显示上传结果
                batchUploadResultDialog.results = response || [];
                batchUploadResultDialog.visible = true;
                
                // 计算成功和失败数量
                const successCount = response.filter(item => item.success).length;
                const failCount = response.length - successCount;
                
                if (failCount === 0) {
                    ElMessage.success(`成功导入 ${successCount} 条记录`);
                } else {
                    ElMessage.warning(`导入完成：成功 ${successCount} 条，失败 ${failCount} 条`);
                }
                
                // 重新加载关联备件
                loadRelatedParts(usageDetailDialog.usageId);
            } catch (error) {
                console.error('上传备件关联失败:', error);
                ElMessage.error('上传备件关联失败: ' + error.message);
            }
        };

        // 查看爆炸图
        const viewExplodedImage = (row) => {
            if (row.downloadUrl) {
                imageDialog.imageUrl = row.downloadUrl;
                imageDialog.visible = true;
                console.log('查看爆炸图:', row.downloadUrl);
            } else {
                ElMessage.warning('该用法没有爆炸图');
            }
        };
        
        // 查看配置组合详情
        const viewCombinationDetails = (combination) => {
            try {
                console.log('查看配置组合详情:', combination);
                // 弹出对话框显示配置项明细
                ElMessageBox.alert(
                    `<div class="combination-details">
                        <h3>配置组合: ${combination.combinationName}</h3>
                        <div class="config-items">
                            ${combination.configItems && combination.configItems.length > 0 
                                ? combination.configItems.map(item => 
                                    `<div class="config-item">
                                        <span class="item-name">${item.itemName}:</span>
                                        <span class="item-value">${item.itemValue}</span>
                                    </div>`
                                ).join('')
                                : '<p>暂无配置项</p>'
                            }
                        </div>
                    </div>`,
                    '配置组合详情',
                    {
                        dangerouslyUseHTMLString: true,
                        confirmButtonText: '关闭'
                    }
                );
            } catch (error) {
                console.error('查看配置组合详情失败:', error);
                ElMessage.error('查看配置组合详情失败: ' + error.message);
            }
        };
        
        // 删除配置组合
        const deleteCombination = async (combination) => {
            try {
                await ElMessageBox.confirm(
                    `确定要删除配置组合"${combination.combinationName}"吗？此操作不可恢复。`,
                    '删除确认',
                    {
                        confirmButtonText: '确定删除',
                        cancelButtonText: '取消',
                        type: 'warning'
                    }
                );
                
                console.log('删除配置组合:', combination.id);
                // 发送请求删除配置组合
                await request(`/ap1/v1/usage/delete_combination?combinationId=${combination.id}`, {
                    method: 'POST'
                });
                
                // 从列表中移除该组合
                const index = usageDetailDialog.combinations.findIndex(item => item.id === combination.id);
                if (index !== -1) {
                    usageDetailDialog.combinations.splice(index, 1);
                }
                
                ElMessage.success('删除配置组合成功');
            } catch (error) {
                if (error !== 'cancel') {
                    console.error('删除配置组合失败:', error);
                    ElMessage.error('删除配置组合失败: ' + error.message);
                }
            }
        };

        // 删除用法（逻辑删除）
        const deleteUsage = async (row) => {
            try {
                await ElMessageBox.confirm(
                    `确定要删除用法"${row.usageName}"吗？删除后可以在"仅已删除"状态中恢复。`,
                    '删除确认',
                    {
                        confirmButtonText: '确定删除',
                        cancelButtonText: '取消',
                        type: 'warning'
                    }
                );

                console.log('删除用法:', row.id);
                await request(`/ap1/v1/usage/delete_usage?usageId=${row.id}`, {
                    method: 'POST'
                });

                ElMessage.success('删除成功，可在"仅已删除"状态中恢复');
                // 重新查询列表
                await queryUsages();
            } catch (error) {
                if (error !== 'cancel') {
                    console.error('删除用法失败:', error);
                    ElMessage.error('删除失败: ' + (error.message || '未知错误'));
                }
            }
        };

        // 恢复用法
        const restoreUsage = async (row) => {
            try {
                await ElMessageBox.confirm(
                    `确定要恢复用法"${row.usageName}"吗？恢复后用法将重新可用。`,
                    '恢复确认',
                    {
                        confirmButtonText: '确定恢复',
                        cancelButtonText: '取消',
                        type: 'success'
                    }
                );

                console.log('恢复用法:', row.id);
                await request(`/ap1/v1/usage/restore_usage?usageId=${row.id}`, {
                    method: 'POST'
                });

                ElMessage.success('恢复成功');
                // 重新查询列表
                await queryUsages();
            } catch (error) {
                if (error !== 'cancel') {
                    console.error('恢复用法失败:', error);
                    ElMessage.error('恢复失败: ' + (error.message || '未知错误'));
                }
            }
        };

        // 处理文件选择
        const handleFileChange = (file) => {
            usageDialog.form.explodedViewFile = file.raw;
            console.log('选择文件:', file.name);
        };

        // 添加配置组合
        const addCombination = () => {
            usageDialog.form.combinations.push({
                combinationName: '',
                sortOrder: usageDialog.form.combinations.length + 1,
                configItemIds: [],
                activeTab: categories.value.length > 0 ? categories.value[0].id.toString() : '1',
                categoryConfigItems: {} // 存储各个类别的配置项 {categoryId: [items]}
            });
            console.log('添加配置组合，当前数量:', usageDialog.form.combinations.length);
        };

        // 移除配置组合
        const removeCombination = (index) => {
            usageDialog.form.combinations.splice(index, 1);
            // 重新排序
            usageDialog.form.combinations.forEach((combination, idx) => {
                combination.sortOrder = idx + 1;
            });
            console.log('移除配置组合，当前数量:', usageDialog.form.combinations.length);
        };

        // 为特定类别加载配置项
        const loadConfigItemsForCategory = async (combination, categoryId) => {
            try {
                console.log('加载配置类别下的配置项:', categoryId);
                const data = await request(`/api/v1/config/get_config_items?categoryId=${categoryId}`);

                // 存储到对应类别
                if (!combination.categoryConfigItems) {
                    combination.categoryConfigItems = {};
                }
                combination.categoryConfigItems[categoryId] = data || [];

                console.log(`加载类别${categoryId}的配置项成功，共`, data?.length || 0, '个');
            } catch (error) {
                console.error('加载配置项失败:', error);
                ElMessage.error('加载配置项失败');
            }
        };

        // 移除选中的配置项
        const removeConfigItem = (combination, itemId) => {
            const index = combination.configItemIds.indexOf(itemId);
            if (index > -1) {
                combination.configItemIds.splice(index, 1);
            }
            console.log('移除配置项:', itemId);
        };

        // 获取配置项名称（用于显示已选择的配置项）
        const getConfigItemName = (combination, itemId) => {
            // 遍历所有类别的配置项，找到对应的配置项
            for (const categoryId in combination.categoryConfigItems) {
                const items = combination.categoryConfigItems[categoryId];
                const item = items.find(item => item.id === itemId);
                if (item) {
                    return `${item.itemName} (${item.itemValue})`;
                }
            }
            return `配置项 ${itemId}`;
        };

        // ==================== 模板管理方法 ====================

        // 查询模板列表
        const queryTemplates = async () => {
            templateLoading.value = true;
            try {
                console.log('查询模板列表，查询条件:', templateQuery);
                const data = await request('/api/v1/structure/template/query_templates', {
                    method: 'POST',
                    body: JSON.stringify({
                        templateCode: templateQuery.templateCode || null,
                        status: templateQuery.status || null,
                        nameKeyword: templateQuery.nameKeyword || null,
                        pageNo: 1,
                        pageSize: 1000
                    })
                });

                console.log('模板API返回的原始数据:', data);
                console.log('数据类型:', typeof data);
                console.log('是否为数组:', Array.isArray(data));
                if (data && typeof data === 'object') {
                    console.log('数据的所有键:', Object.keys(data));
                }

                // 处理分页数据 - 根据实际API响应结构调整
                let templateList = [];
                if (data && Array.isArray(data.templates)) {
                    templateList = data.templates;
                    console.log('使用 data.templates，数量:', templateList.length);
                } else if (data && Array.isArray(data.records)) {
                    templateList = data.records;
                    console.log('使用 data.records，数量:', templateList.length);
                } else if (data && Array.isArray(data.list)) {
                    templateList = data.list;
                    console.log('使用 data.list，数量:', templateList.length);
                } else if (data && Array.isArray(data.data)) {
                    templateList = data.data;
                    console.log('使用 data.data，数量:', templateList.length);
                } else if (Array.isArray(data)) {
                    templateList = data;
                    console.log('直接使用 data 数组，数量:', templateList.length);
                } else {
                    templateList = [];
                    console.log('无法解析数据结构，设置为空数组');
                }

                templates.value = templateList;
                console.log('最终设置的模板列表:', templates.value);
                console.log('查询模板列表成功，共', templates.value.length, '条记录');
            } catch (error) {
                console.error('查询模板列表失败:', error);
                templates.value = [];
            } finally {
                templateLoading.value = false;
            }
        };

        // 清空模板查询条件
        const clearTemplateQuery = () => {
            templateQuery.templateCode = '';
            templateQuery.nameKeyword = '';
            templateQuery.status = '';
            queryTemplates();
        };

        // 显示创建模板对话框
        const showCreateTemplateDialog = async () => {
            templateDialog.visible = true;
            templateDialog.isEdit = false;
            Object.assign(templateDialog.form, {
                templateId: null,
                templateCode: '',
                templateName: '',
                templateDesc: '',
                version: '',
                creator: '管理员',
                nodes: []
            });
        };

        // 编辑模板
        const editTemplate = (row) => {
            templateDialog.visible = true;
            templateDialog.isEdit = true;
            Object.assign(templateDialog.form, {
                templateId: row.id,
                templateCode: row.templateCode,
                templateName: row.templateName,
                templateDesc: row.templateDesc,
                version: row.version
            });
            console.log('编辑模板:', templateDialog.form);
        };

        // 复制模板（创建新版本）
        const copyTemplate = async (row) => {
            try {
                await ElMessageBox.prompt('请输入新版本号', '复制模板版本', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    inputPattern: /^.+$/,
                    inputErrorMessage: '版本号不能为空'
                }).then(async ({ value }) => {
                    console.log('复制模板版本:', row.id, '新版本:', value);
                    await request('/api/v1/structure/template/create_new_version', {
                        method: 'POST',
                        body: JSON.stringify({
                            templateId: row.id,
                            newVersion: value,
                            creator: '管理员'
                        })
                    });
                    ElMessage.success('复制成功');
                    await queryTemplates();
                });
            } catch (error) {
                if (error !== 'cancel') {
                    console.error('复制模板失败:', error);
                    ElMessage.error('复制失败: ' + (error.message || '未知错误'));
                }
            }
        };

        // 切换模板状态
        const toggleTemplateStatus = async (row) => {
            const newStatus = row.status === 'ENABLED' ? 'DISABLED' : 'ENABLED';
            const action = newStatus === 'ENABLED' ? '启用' : '禁用';

            try {
                await ElMessageBox.confirm(
                    `确定要${action}模板"${row.templateName}"吗？`,
                    `${action}确认`,
                    {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning'
                    }
                );

                console.log(`${action}模板:`, row.id);
                await request(`/api/v1/structure/template/${newStatus === 'ENABLED' ? 'enable' : 'disable'}_template`, {
                    method: 'POST',
                    body: JSON.stringify({ templateId: row.id })
                });

                ElMessage.success(`${action}成功`);
                await queryTemplates();
            } catch (error) {
                if (error !== 'cancel') {
                    console.error(`${action}模板失败:`, error);
                    ElMessage.error(`${action}失败: ` + (error.message || '未知错误'));
                }
            }
        };

        // 查看模板详情
        const viewTemplateDetail = async (row) => {
            try {
                console.log('查看模板详情:', row.id);
                const data = await request(`/api/v1/structure/template/get_template_detail?templateId=${row.id}`);

                templateDetailDialog.data = data;
                templateDetailDialog.templateName = row.templateName;
                templateDetailDialog.visible = true;

                console.log('获取模板详情成功，数据结构:', data);
                console.log('节点树数据:', data.nodeTree);
                if (data.nodeTree && data.nodeTree.length > 0) {
                    console.log('第一个节点示例:', data.nodeTree[0]);
                }
            } catch (error) {
                console.error('获取模板详情失败:', error);
                ElMessage.error('获取详情失败: ' + (error.message || '未知错误'));
            }
        };

        // 提交模板
        const submitTemplate = async () => {
            templateDialog.loading = true;
            try {
                if (templateDialog.isEdit) {
                    // 更新模板
                    await request('/api/v1/structure/template/update_template', {
                        method: 'POST',
                        body: JSON.stringify({
                            templateId: templateDialog.form.templateId,
                            templateName: templateDialog.form.templateName,
                            templateDesc: templateDialog.form.templateDesc
                        })
                    });
                    ElMessage.success('更新成功');
                } else {
                    // 创建模板
                    await request('/api/v1/structure/template/create', {
                        method: 'POST',
                        body: JSON.stringify({
                            templateCode: templateDialog.form.templateCode,
                            templateName: templateDialog.form.templateName,
                            templateDesc: templateDialog.form.templateDesc,
                            version: templateDialog.form.version,
                            creator: templateDialog.form.creator,
                            nodes: templateDialog.form.nodes
                        })
                    });
                    ElMessage.success('创建成功');
                }

                templateDialog.visible = false;
                await queryTemplates();
            } catch (error) {
                console.error('提交模板失败:', error);
                ElMessage.error('操作失败: ' + (error.message || '未知错误'));
            } finally {
                templateDialog.loading = false;
            }
        };

        // 添加模板节点
        const addTemplateNode = () => {
            templateNodeDialog.visible = true;
            templateNodeDialog.isEdit = false;
            templateNodeDialog.parentNode = null;
            Object.assign(templateNodeDialog.form, {
                tempId: null,
                nodeName: '',
                nodeType: 'CATEGORY',
                sortOrder: templateDialog.form.nodes.length + 1,
                nodeDesc: ''
            });
        };

        // 添加子节点
        const addChildTemplateNode = (parentNode) => {
            templateNodeDialog.visible = true;
            templateNodeDialog.isEdit = false;
            templateNodeDialog.parentNode = parentNode;
            Object.assign(templateNodeDialog.form, {
                tempId: null,
                nodeName: '',
                nodeType: 'GROUP',
                sortOrder: (parentNode.children?.length || 0) + 1,
                nodeDesc: ''
            });
        };

        // 编辑模板节点
        const editTemplateNode = (node) => {
            templateNodeDialog.visible = true;
            templateNodeDialog.isEdit = true;
            templateNodeDialog.parentNode = null;
            Object.assign(templateNodeDialog.form, {
                tempId: node.tempId,
                nodeName: node.nodeName,
                nodeType: node.nodeType,
                sortOrder: node.sortOrder,
                nodeDesc: node.nodeDesc
            });
        };

        // 删除模板节点
        const deleteTemplateNode = (node) => {
            ElMessageBox.confirm(
                `确定要删除节点"${node.nodeName}"吗？删除后其所有子节点也将被删除。`,
                '删除确认',
                {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }
            ).then(() => {
                // 递归删除节点
                const removeNodeFromTree = (nodes, targetId) => {
                    for (let i = 0; i < nodes.length; i++) {
                        if (nodes[i].tempId === targetId) {
                            nodes.splice(i, 1);
                            return true;
                        }
                        if (nodes[i].children && removeNodeFromTree(nodes[i].children, targetId)) {
                            return true;
                        }
                    }
                    return false;
                };

                removeNodeFromTree(templateDialog.form.nodes, node.tempId);
                ElMessage.success('删除成功');
            }).catch(() => {
                console.log('用户取消删除');
            });
        };

        // 提交模板节点
        const submitTemplateNode = () => {
            templateNodeDialog.loading = true;
            try {
                const nodeData = {
                    tempId: templateNodeDialog.form.tempId || Date.now() + Math.random(),
                    nodeName: templateNodeDialog.form.nodeName,
                    nodeType: templateNodeDialog.form.nodeType,
                    sortOrder: templateNodeDialog.form.sortOrder,
                    nodeDesc: templateNodeDialog.form.nodeDesc,
                    children: []
                };

                if (templateNodeDialog.isEdit) {
                    // 编辑节点
                    const updateNodeInTree = (nodes, targetId, newData) => {
                        for (let node of nodes) {
                            if (node.tempId === targetId) {
                                Object.assign(node, newData);
                                return true;
                            }
                            if (node.children && updateNodeInTree(node.children, targetId, newData)) {
                                return true;
                            }
                        }
                        return false;
                    };

                    updateNodeInTree(templateDialog.form.nodes, templateNodeDialog.form.tempId, nodeData);
                } else {
                    // 添加节点
                    if (templateNodeDialog.parentNode) {
                        // 添加子节点
                        if (!templateNodeDialog.parentNode.children) {
                            templateNodeDialog.parentNode.children = [];
                        }
                        templateNodeDialog.parentNode.children.push(nodeData);
                    } else {
                        // 添加根节点
                        templateDialog.form.nodes.push(nodeData);
                    }
                }

                templateNodeDialog.visible = false;
                ElMessage.success(templateNodeDialog.isEdit ? '更新成功' : '添加成功');
            } catch (error) {
                console.error('提交节点失败:', error);
                ElMessage.error('操作失败: ' + (error.message || '未知错误'));
            } finally {
                templateNodeDialog.loading = false;
            }
        };



        // 允许拖拽放置
        const allowTemplateDrop = (draggingNode, dropNode, type) => {
            // 可以添加拖拽规则
            return true;
        };

        // 处理节点拖拽
        const handleTemplateNodeDrop = (draggingNode, dropNode, dropType, ev) => {
            console.log('节点拖拽:', draggingNode, dropNode, dropType);
            // 可以在这里处理拖拽后的逻辑
        };

        // ==================== 右键菜单功能 ====================

        // 隐藏右键菜单
        const hideContextMenu = (clearData = true) => {
            contextMenu.visible = false;
            if (clearData) {
                contextMenu.currentNode = null;
                contextMenu.currentData = null;
                contextMenu.currentTreeNode = null;
            }
        };

        // 处理模板节点右键菜单
        const handleTemplateNodeContextMenu = (event, data, node) => {
            event.preventDefault();

            // 确保我们有正确的数据
            const nodeData = data || (node ? node.data : null);

            if (!nodeData) {
                ElMessage.error('无法获取节点数据');
                return;
            }

            contextMenu.visible = true;
            contextMenu.x = event.clientX;
            contextMenu.y = event.clientY;
            contextMenu.type = 'template';
            contextMenu.currentNode = node;
            contextMenu.currentData = nodeData;
            contextMenu.currentTreeNode = node;
        };

        // 处理实例节点右键菜单
        const handleInstanceNodeContextMenu = (event, data, node) => {
            event.preventDefault();

            // 确保我们有正确的数据
            const nodeData = data || (node ? node.data : null);

            if (!nodeData) {
                ElMessage.error('无法获取节点数据');
                return;
            }

            contextMenu.visible = true;
            contextMenu.x = event.clientX;
            contextMenu.y = event.clientY;
            contextMenu.type = 'instance';
            contextMenu.currentNode = node;
            contextMenu.currentData = nodeData;
            contextMenu.currentTreeNode = node;
        };

        // 处理实例节点点击事件
        const handleInstanceNodeClick = async (data, node) => {
            // 如果是用法节点，跳转到用法管理页面
            if (data.nodeType === 'USAGE') {
                try {
                    // 关闭实例详情对话框
                    instanceDetailDialog.visible = false;

                    // 切换到用法管理页面
                    activeTab.value = 'usage';

                    // 等待页面切换完成
                    await nextTick();

                    // 根据节点信息查找对应的用法
                    await queryUsages();

                    // 查找匹配的用法（通过节点名称或其他标识）
                    const matchedUsage = usages.value.find(usage =>
                        usage.usageName === data.nodeName ||
                        usage.usageId === data.usageId ||
                        usage.id === data.relatedUsageId
                    );

                    if (matchedUsage) {
                        // 显示用法详情
                        await viewUsageDetail(matchedUsage);
                        ElMessage.success(`已跳转到用法: ${matchedUsage.usageName}`);
                    } else {
                        // 如果没有找到匹配的用法，显示提示并高亮相关用法
                        ElMessage.info(`正在查找用法: ${data.nodeName}`);

                        // 可以在这里添加搜索逻辑
                        usageQuery.usageName = data.nodeName;
                        await queryUsages();

                        if (usages.value.length > 0) {
                            ElMessage.success(`找到 ${usages.value.length} 个相关用法`);
                        } else {
                            ElMessage.warning(`未找到名称为 "${data.nodeName}" 的用法`);
                        }
                    }
                } catch (error) {
                    console.error('跳转到用法页面失败:', error);
                    ElMessage.error('跳转失败: ' + (error.message || '未知错误'));
                }
            } else {
                // 非用法节点，显示节点信息
                ElMessage.info(`节点信息: ${data.nodeName} [${data.nodeType}]`);
            }
        };

        // 添加子节点
        const addChildNode = async () => {


            // 先保存数据，再隐藏菜单
            const parentData = contextMenu.currentData;
            hideContextMenu(false); // 隐藏菜单但不清空数据

            try {

                // 检查parentData是否存在
                if (!parentData) {
    
                    ElMessage.error('无法获取父节点信息，请重新右键点击节点');
                    return;
                }



                // 检查是否为用法节点，用法节点不能添加子节点
                if (parentData.nodeType === 'USAGE') {
                    ElMessage.warning('用法节点不能添加子节点');
                    return;
                }

                // 确定子节点类型
                let childNodeType = 'USAGE';
                if (!parentData.nodeType || parentData.nodeType === 'CATEGORY') {
                    childNodeType = 'GROUP';
                } else if (parentData.nodeType === 'GROUP') {
                    childNodeType = 'USAGE';
                }

                // 计算排序序号（当前最大排序号+1）
                const siblings = parentData.children || [];
                const maxSortOrder = siblings.length > 0 ? Math.max(...siblings.map(child => child.sortOrder || 0)) : 0;
                const newSortOrder = maxSortOrder + 1;

                console.log('子节点类型:', childNodeType, '排序序号:', newSortOrder);

                // 显示添加节点对话框
                await showAddNodeDialog(parentData, childNodeType, newSortOrder);

            } catch (error) {
                if (error !== 'cancel') {
                    console.error('添加子节点失败:', error);
                    ElMessage.error('添加子节点失败: ' + (error.message || '未知错误'));
                }
            }
        };

        // 编辑当前节点
        const editCurrentNode = async () => {
            const currentData = contextMenu.currentData;
            hideContextMenu(false);

            try {
                const result = await ElMessageBox.prompt('请输入新的节点名称', '编辑节点', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    inputPattern: /^.+$/,
                    inputErrorMessage: '节点名称不能为空',
                    inputValue: currentData.nodeName
                });

                const newNodeName = result.value;
                currentData.nodeName = newNodeName;

                // 调用API更新节点
                await updateNodeToServer(currentData, contextMenu.type);

                ElMessage.success('编辑节点成功');
            } catch (error) {
                if (error !== 'cancel') {
                    console.error('编辑节点失败:', error);
                    ElMessage.error('编辑节点失败: ' + (error.message || '未知错误'));
                }
            }
        };

        // 删除当前节点
        const deleteCurrentNode = async () => {
            const currentData = contextMenu.currentData;
            const currentTreeNode = contextMenu.currentTreeNode;
            hideContextMenu(false);

            try {
                await ElMessageBox.confirm(
                    `确定要删除节点"${currentData.nodeName}"吗？删除后其所有子节点也将被删除。`,
                    '删除确认',
                    {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning'
                    }
                );

                // 先调用API删除节点
                await deleteNodeFromServer(currentData, contextMenu.type);

                // API调用成功后，刷新树数据以确保显示最新状态
                await refreshTreeData(contextMenu.type);

                ElMessage.success('删除节点成功');
            } catch (error) {
                if (error !== 'cancel') {
                    console.error('删除节点失败:', error);
                    ElMessage.error('删除节点失败: ' + (error.message || '未知错误'));
                }
            }
        };

        // 上移节点
        const moveNodeUp = () => {
            const currentData = contextMenu.currentData;
            const currentTreeNode = contextMenu.currentTreeNode;
            hideContextMenu(false);
            moveNode(-1, currentData, currentTreeNode);
        };

        // 下移节点
        const moveNodeDown = () => {
            const currentData = contextMenu.currentData;
            const currentTreeNode = contextMenu.currentTreeNode;
            hideContextMenu(false);
            moveNode(1, currentData, currentTreeNode);
        };

        // 移动节点
        const moveNode = async (direction, currentData, currentTreeNode) => {
            try {
                const parentNode = currentTreeNode.parent;

                let siblings;
                let parentId = null;

                if (parentNode && parentNode.data && parentNode.data.children) {
                    siblings = parentNode.data.children;
                    parentId = parentNode.data.id;
                } else {
                    // 根节点
                    siblings = contextMenu.type === 'template'
                        ? templateDetailDialog.data.nodeTree
                        : instanceDetailDialog.data.nodeTree;
                    parentId = null;
                }

                const currentIndex = siblings.findIndex(node => node.id === currentData.id);
                const newIndex = currentIndex + direction;

                if (newIndex >= 0 && newIndex < siblings.length) {
                    // 计算新的排序序号（目标位置的排序号）
                    const newSortOrder = newIndex + 1;

                    console.log(`移动节点: ${currentData.nodeName}, 从位置 ${currentIndex + 1} 到位置 ${newSortOrder}`);

                    // 调用API移动节点
                    await moveNodeToServer(currentData.id, parentId, newSortOrder, contextMenu.type);

                    // API调用成功后，重新获取最新数据以确保显示正确
                    await refreshTreeData(contextMenu.type);

                    ElMessage.success(direction === -1 ? '上移成功' : '下移成功');
                } else {
                    ElMessage.warning('无法移动到该位置');
                }
            } catch (error) {
                console.error('移动节点失败:', error);
                ElMessage.error('移动节点失败: ' + (error.message || '未知错误'));
            }
        };

        // 调用移动节点API
        const moveNodeToServer = async (nodeId, newParentId, sortOrder, type) => {
            console.log('调用移动节点API:', { nodeId, newParentId, sortOrder, type });

            const requestData = {
                nodeId: nodeId,
                newParentId: newParentId,
                sortOrder: sortOrder
            };

            try {
                if (type === 'template') {
                    // 调用模板移动节点API
                    await request('/api/v1/structure/template/move_node', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        body: JSON.stringify(requestData)
                    });
                } else {
                    // 调用实例移动节点API
                    await request('/api/v1/structure/instance/move_node', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        body: JSON.stringify(requestData)
                    });
                }
                console.log('移动节点API调用成功');
            } catch (error) {
                console.error('移动节点API调用失败:', error);
                throw error;
            }
        };

        // 刷新树数据
        const refreshTreeData = async (type) => {
            try {
                console.log('刷新树数据:', type);

                if (type === 'template') {
                    // 重新获取模板详情数据
                    const templateId = templateDetailDialog.data.id;
                    const data = await request(`/api/v1/structure/template/get_template_detail?templateId=${templateId}`);
                    templateDetailDialog.data = data;
                    console.log('模板树数据刷新成功');
                } else {
                    // 重新获取实例详情数据
                    const instanceId = instanceDetailDialog.data.id;
                    const data = await request(`/api/v1/structure/instance/get_instance_detail?instanceId=${instanceId}`);
                    instanceDetailDialog.data = data;
                    console.log('实例树数据刷新成功');
                }
            } catch (error) {
                console.error('刷新树数据失败:', error);
                ElMessage.error('刷新数据失败: ' + (error.message || '未知错误'));
            }
        };

        // 添加模板根节点
        const addTemplateRootNode = async () => {
            try {
                // 计算排序序号
                const currentRoots = templateDetailDialog.data.nodeTree || [];
                const maxSortOrder = currentRoots.length > 0 ? Math.max(...currentRoots.map(node => node.sortOrder || 0)) : 0;
                const newSortOrder = maxSortOrder + 1;

                // 设置上下文
                contextMenu.type = 'template';

                // 显示添加节点对话框
                await showAddNodeDialog(null, 'CATEGORY', newSortOrder);

            } catch (error) {
                console.error('添加根节点失败:', error);
                ElMessage.error('添加根节点失败: ' + (error.message || '未知错误'));
            }
        };

        // 添加实例根节点
        const addInstanceRootNode = async () => {
            try {
                // 计算排序序号
                const currentRoots = instanceDetailDialog.data.nodeTree || [];
                const maxSortOrder = currentRoots.length > 0 ? Math.max(...currentRoots.map(node => node.sortOrder || 0)) : 0;
                const newSortOrder = maxSortOrder + 1;

                // 设置上下文
                contextMenu.type = 'instance';

                // 显示添加节点对话框
                await showAddNodeDialog(null, 'CATEGORY', newSortOrder);

            } catch (error) {
                console.error('添加根节点失败:', error);
                ElMessage.error('添加根节点失败: ' + (error.message || '未知错误'));
            }
        };

        // 树组件引用
        const templateTreeRef = ref(null);
        const instanceTreeRef = ref(null);

        // 展开所有模板节点
        const expandAllTemplateNodes = () => {
            if (templateTreeRef.value) {
                // 获取所有节点并展开
                const allNodes = getAllTreeNodes(templateDetailDialog.data.nodeTree);
                allNodes.forEach(node => {
                    templateTreeRef.value.setExpanded(node.id, true);
                });
                console.log('展开所有模板节点');
            }
        };

        // 收起所有模板节点
        const collapseAllTemplateNodes = () => {
            if (templateTreeRef.value) {
                // 获取所有节点并收起
                const allNodes = getAllTreeNodes(templateDetailDialog.data.nodeTree);
                allNodes.forEach(node => {
                    templateTreeRef.value.setExpanded(node.id, false);
                });
                console.log('收起所有模板节点');
            }
        };

        // 展开所有实例节点
        const expandAllInstanceNodes = () => {
            if (instanceTreeRef.value) {
                // 获取所有节点并展开
                const allNodes = getAllTreeNodes(instanceDetailDialog.data.nodeTree);
                allNodes.forEach(node => {
                    instanceTreeRef.value.setExpanded(node.id, true);
                });
                console.log('展开所有实例节点');
            }
        };

        // 收起所有实例节点
        const collapseAllInstanceNodes = () => {
            if (instanceTreeRef.value) {
                // 获取所有节点并收起
                const allNodes = getAllTreeNodes(instanceDetailDialog.data.nodeTree);
                allNodes.forEach(node => {
                    instanceTreeRef.value.setExpanded(node.id, false);
                });
                console.log('收起所有实例节点');
            }
        };

        // 递归获取所有树节点
        const getAllTreeNodes = (nodes) => {
            let allNodes = [];
            if (!nodes) return allNodes;

            nodes.forEach(node => {
                allNodes.push(node);
                if (node.children && node.children.length > 0) {
                    allNodes = allNodes.concat(getAllTreeNodes(node.children));
                }
            });
            return allNodes;
        };

        // 保存节点到服务器（模拟）
        const saveNodeToServer = async (nodeData, parentData, type) => {
            console.log('保存节点到服务器:', nodeData, parentData, type);
            // 这里可以调用实际的API来保存节点
            // 例如：await request('/api/v1/structure/template/add_node', { ... })
        };

        // 更新节点到服务器（模拟）
        const updateNodeToServer = async (nodeData, type) => {
            console.log('更新节点到服务器:', nodeData, type);
            // 这里可以调用实际的API来更新节点
            // 例如：await request('/api/v1/structure/template/update_node', { ... })
        };

        // 删除节点从服务器
        const deleteNodeFromServer = async (nodeData, type) => {
            console.log('从服务器删除节点:', nodeData, type);

            try {
                if (type === 'template') {
                    // 调用模板删除节点API
                    await request('/api/v1/structure/template/delete_node', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded',
                        },
                        body: `nodeId=${nodeData.id}`
                    });
                } else {
                    // 调用实例删除节点API
                    await request('/api/v1/structure/instance/delete_node', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded',
                        },
                        body: `nodeId=${nodeData.id}`
                    });
                }
                console.log('删除节点API调用成功');
            } catch (error) {
                console.error('删除节点API调用失败:', error);
                throw error; // 重新抛出错误，让调用方处理
            }
        };

        // 显示添加节点对话框
        const showAddNodeDialog = async (parentData, nodeType, sortOrder) => {
            nodeDialog.visible = true;
            nodeDialog.isEdit = false;
            nodeDialog.type = contextMenu.type;
            nodeDialog.parentData = parentData;

            // 重置表单
            Object.assign(nodeDialog.form, {
                nodeName: '',
                nodeNameEn: '',
                nodeType: nodeType,
                id: null,
                sortOrder: sortOrder
            });
        };

        // 提交节点
        const submitNode = async () => {
            nodeDialog.loading = true;
            try {
                const formData = nodeDialog.form;
                const parentData = nodeDialog.parentData;
                const isTemplate = nodeDialog.type === 'template';

                // 构建API请求参数
                const requestData = {
                    nodeName: formData.nodeName,
                    nodeNameEn: formData.nodeNameEn || null,
                    nodeType: formData.nodeType,
                    sortOrder: formData.sortOrder,
                    creator: '管理员'
                };

                // 根据节点类型设置对应的ID字段
                if (formData.nodeType === 'CATEGORY') {
                    requestData.categoryId = formData.id;
                    requestData.groupId = null;
                    if (!isTemplate) requestData.usageId = null;
                } else if (formData.nodeType === 'GROUP') {
                    requestData.categoryId = null;
                    requestData.groupId = formData.id;
                    if (!isTemplate) requestData.usageId = null;
                } else if (formData.nodeType === 'USAGE') {
                    requestData.categoryId = null;
                    requestData.groupId = null;
                    if (!isTemplate) requestData.usageId = formData.id;
                }

                // 设置父节点ID和模板/实例ID
                if (isTemplate) {
                    requestData.templateId = templateDetailDialog.data.id;
                    requestData.parentNodeId = parentData ? parentData.id : null;

                    // 调用模板添加节点API
                    const response = await request('/api/v1/structure/template/add_node', {
                        method: 'POST',
                        body: JSON.stringify(requestData)
                    });

                    // 添加到前端树结构
                    const newNode = {
                        id: response.id,
                        nodeName: response.nodeName,
                        nodeNameEn: response.nodeNameEn,
                        nodeType: response.nodeType,
                        sortOrder: response.sortOrder,
                        children: []
                    };

                    if (parentData) {
                        if (!parentData.children) parentData.children = [];
                        parentData.children.push(newNode);
                    } else {
                        if (!templateDetailDialog.data.nodeTree) templateDetailDialog.data.nodeTree = [];
                        templateDetailDialog.data.nodeTree.push(newNode);
                    }
                } else {
                    requestData.instanceId = instanceDetailDialog.data.id;
                    requestData.parentNodeId = parentData ? parentData.id : null;

                    // 调用实例添加节点API
                    const response = await request('/api/v1/structure/instance/add_node', {
                        method: 'POST',
                        body: JSON.stringify(requestData)
                    });

                    // 添加到前端树结构
                    const newNode = {
                        id: response.id,
                        nodeName: response.nodeName,
                        nodeNameEn: response.nodeNameEn,
                        nodeType: response.nodeType,
                        sortOrder: response.sortOrder,
                        children: []
                    };

                    if (parentData) {
                        if (!parentData.children) parentData.children = [];
                        parentData.children.push(newNode);
                    } else {
                        if (!instanceDetailDialog.data.nodeTree) instanceDetailDialog.data.nodeTree = [];
                        instanceDetailDialog.data.nodeTree.push(newNode);
                    }
                }

                nodeDialog.visible = false;

                // 刷新树数据以确保显示最新状态
                await refreshTreeData(nodeDialog.type);

                ElMessage.success('添加节点成功');

            } catch (error) {
                console.error('添加节点失败:', error);
                ElMessage.error('添加节点失败: ' + (error.message || '未知错误'));
            } finally {
                nodeDialog.loading = false;
            }
        };

        // ==================== 实例管理方法 ====================

        // 查询实例列表
        const queryInstances = async () => {
            instanceLoading.value = true;
            try {
                console.log('查询实例列表，查询条件:', instanceQuery);
                const data = await request('/api/v1/structure/instance/query_instances', {
                    method: 'POST',
                    body: JSON.stringify({
                        instanceCode: instanceQuery.instanceCode || null,
                        nameKeyword: instanceQuery.nameKeyword || null,
                        status: instanceQuery.status || null,
                        seriesId: null,
                        modelId: null,
                        pageNo: 1,
                        pageSize: 1000
                    })
                });

                console.log('实例API返回的原始数据:', data);
                console.log('数据类型:', typeof data);
                console.log('是否为数组:', Array.isArray(data));
                if (data && typeof data === 'object') {
                    console.log('数据的所有键:', Object.keys(data));
                }

                // 处理分页数据 - 根据实际API响应结构调整
                let instanceList = [];
                if (data && Array.isArray(data.instances)) {
                    instanceList = data.instances;
                    console.log('使用 data.instances，数量:', instanceList.length);
                } else if (data && Array.isArray(data.records)) {
                    instanceList = data.records;
                    console.log('使用 data.records，数量:', instanceList.length);
                } else if (data && Array.isArray(data.list)) {
                    instanceList = data.list;
                    console.log('使用 data.list，数量:', instanceList.length);
                } else if (data && Array.isArray(data.data)) {
                    instanceList = data.data;
                    console.log('使用 data.data，数量:', instanceList.length);
                } else if (Array.isArray(data)) {
                    instanceList = data;
                    console.log('直接使用 data 数组，数量:', instanceList.length);
                } else {
                    instanceList = [];
                    console.log('无法解析数据结构，设置为空数组');
                }

                instances.value = instanceList;
                console.log('最终设置的实例列表:', instances.value);
                console.log('查询实例列表成功，共', instances.value.length, '条记录');
            } catch (error) {
                console.error('查询实例列表失败:', error);
                instances.value = [];
            } finally {
                instanceLoading.value = false;
            }
        };

        // 清空实例查询条件
        const clearInstanceQuery = () => {
            instanceQuery.instanceCode = '';
            instanceQuery.nameKeyword = '';
            instanceQuery.status = '';
            queryInstances();
        };

        // 加载可用模板列表
        const loadAvailableTemplates = async () => {
            try {
                const data = await request('/api/v1/structure/template/query_templates', {
                    method: 'POST',
                    body: JSON.stringify({
                        templateCode: null,
                        status: 'ENABLED', // 只加载启用的模板
                        nameKeyword: null,
                        pageNo: 1,
                        pageSize: 1000
                    })
                });

                if (data && Array.isArray(data.records)) {
                    availableTemplates.value = data.records;
                } else if (Array.isArray(data)) {
                    availableTemplates.value = data;
                } else {
                    availableTemplates.value = [];
                }
            } catch (error) {
                console.error('加载可用模板失败:', error);
                availableTemplates.value = [];
            }
        };

        // 显示创建实例对话框
        const showCreateInstanceDialog = async () => {
            await loadAvailableTemplates();
            instanceDialog.visible = true;
            instanceDialog.isEdit = false;
            Object.assign(instanceDialog.form, {
                instanceId: null,
                templateId: null,
                instanceCode: '',
                instanceName: '',
                instanceDesc: '',
                seriesId: null,
                modelId: null,
                instanceVersion: '',
                creator: '管理员'
            });
        };

        // 模板选择变化处理
        const onTemplateChange = (templateId) => {
            console.log('选择模板:', templateId);
            // 可以在这里根据模板自动填充一些信息
        };

        // 复制实例（创建新版本）
        const copyInstance = async (row) => {
            try {
                await ElMessageBox.prompt('请输入新版本号', '复制实例版本', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    inputPattern: /^.+$/,
                    inputErrorMessage: '版本号不能为空'
                }).then(async ({ value }) => {
                    console.log('复制实例版本:', row.id, '新版本:', value);
                    await request('/api/v1/structure/instance/create_new_version', {
                        method: 'POST',
                        body: JSON.stringify({
                            instanceId: row.id,
                            newVersion: value,
                            instanceDesc: row.instanceDesc,
                            creator: '管理员'
                        })
                    });
                    ElMessage.success('复制成功');
                    await queryInstances();
                });
            } catch (error) {
                if (error !== 'cancel') {
                    console.error('复制实例失败:', error);
                    ElMessage.error('复制失败: ' + (error.message || '未知错误'));
                }
            }
        };

        // 切换实例发布状态
        const toggleInstancePublish = async (row) => {
            const isPublished = row.isPublished;
            const action = isPublished ? '取消发布' : '发布';

            try {
                await ElMessageBox.confirm(
                    `确定要${action}实例"${row.instanceName}"吗？`,
                    `${action}确认`,
                    {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning'
                    }
                );

                console.log(`${action}实例:`, row.id);

                if (isPublished) {
                    // 取消发布
                    await request(`/api/v1/structure/instance/unpublish_instance?instanceId=${row.id}`, {
                        method: 'POST'
                    });
                } else {
                    // 发布实例
                    await request('/api/v1/structure/instance/publish_instance', {
                        method: 'POST',
                        body: JSON.stringify({
                            instanceId: row.id,
                            effectiveTime: new Date().toISOString()
                        })
                    });
                }

                ElMessage.success(`${action}成功`);
                await queryInstances();

                // 如果实例详情对话框是打开的，并且是当前操作的实例，则刷新详情数据
                if (instanceDetailDialog.visible && instanceDetailDialog.data && instanceDetailDialog.data.id === row.id) {
                    const updatedData = await request(`/api/v1/structure/instance/get_instance_detail?instanceId=${row.id}`);

                    // 如果API返回的instanceName为空，使用列表中的instanceName
                    if (!updatedData.instanceName && row.instanceName) {
                        updatedData.instanceName = row.instanceName;
                    }

                    instanceDetailDialog.data = updatedData;
                }
            } catch (error) {
                if (error !== 'cancel') {
                    console.error(`${action}实例失败:`, error);
                    ElMessage.error(`${action}失败: ` + (error.message || '未知错误'));
                }
            }
        };

        // 切换实例状态
        const toggleInstanceStatus = async (row) => {
            const newStatus = row.status === 'ENABLED' ? 'DISABLED' : 'ENABLED';
            const action = newStatus === 'ENABLED' ? '启用' : '禁用';

            try {
                await ElMessageBox.confirm(
                    `确定要${action}实例"${row.instanceName}"吗？`,
                    `${action}确认`,
                    {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning'
                    }
                );

                console.log(`${action}实例:`, row.id);
                await request(`/api/v1/structure/instance/${newStatus === 'ENABLED' ? 'enable' : 'disable'}_instance?instanceId=${row.id}`, {
                    method: 'POST'
                });

                ElMessage.success(`${action}成功`);
                await queryInstances();

                // 如果实例详情对话框是打开的，并且是当前操作的实例，则刷新详情数据
                if (instanceDetailDialog.visible && instanceDetailDialog.data && instanceDetailDialog.data.id === row.id) {
                    const updatedData = await request(`/api/v1/structure/instance/get_instance_detail?instanceId=${row.id}`);

                    // 如果API返回的instanceName为空，使用列表中的instanceName
                    if (!updatedData.instanceName && row.instanceName) {
                        updatedData.instanceName = row.instanceName;
                    }

                    instanceDetailDialog.data = updatedData;
                }
            } catch (error) {
                if (error !== 'cancel') {
                    console.error(`${action}实例失败:`, error);
                    ElMessage.error(`${action}失败: ` + (error.message || '未知错误'));
                }
            }
        };

        // 查看实例详情
        const viewInstanceDetail = async (row) => {
            try {
                const data = await request(`/api/v1/structure/instance/get_instance_detail?instanceId=${row.id}`);

                // 如果API返回的instanceName为空，使用列表中的instanceName
                if (!data.instanceName && row.instanceName) {
                    data.instanceName = row.instanceName;
                }

                instanceDetailDialog.data = data;
                instanceDetailDialog.instanceName = row.instanceName;
                instanceDetailDialog.visible = true;
            } catch (error) {
                console.error('获取实例详情失败:', error);
                ElMessage.error('获取详情失败: ' + (error.message || '未知错误'));
            }
        };

        // 实例对比
        const compareInstance = async (row) => {
            // 加载可用实例列表
            await queryInstances();
            availableInstances.value = instances.value;

            instanceCompareDialog.visible = true;
            instanceCompareDialog.instance1Id = row.id;
            instanceCompareDialog.instance2Id = null;
            instanceCompareDialog.compareData = null;
        };

        // 加载对比数据
        const loadCompareData = async () => {
            if (!instanceCompareDialog.instance1Id || !instanceCompareDialog.instance2Id) {
                instanceCompareDialog.compareData = null;
                return;
            }

            try {
                console.log('加载实例对比数据:', instanceCompareDialog.instance1Id, instanceCompareDialog.instance2Id);

                // 并行获取两个实例的详细信息和差异对比结果
                const [instance1Data, instance2Data, compareResult] = await Promise.all([
                    request(`/api/v1/structure/instance/get_instance_detail?instanceId=${instanceCompareDialog.instance1Id}`),
                    request(`/api/v1/structure/instance/get_instance_detail?instanceId=${instanceCompareDialog.instance2Id}`),
                    request(`/api/v1/structure/instance/compare_instances?instanceId1=${instanceCompareDialog.instance1Id}&instanceId2=${instanceCompareDialog.instance2Id}`, {
                        method: 'POST'
                    })
                ]);

                // 组合数据
                instanceCompareDialog.compareData = {
                    instance1: instance1Data,
                    instance2: instance2Data,
                    differences: compareResult
                };

                console.log('加载对比数据成功:', instanceCompareDialog.compareData);

                // 更新差异统计
                instanceCompareDialog.diffStats = {
                    added: compareResult.totalAdded || 0,
                    removed: compareResult.totalRemoved || 0,
                    modified: compareResult.totalModified || 0
                };
            } catch (error) {
                console.error('加载对比数据失败:', error);
                ElMessage.error('加载对比数据失败: ' + (error.message || '未知错误'));
                instanceCompareDialog.compareData = null;
            }
        };

        // 获取格式化的差异行数据
        const getFormattedDiffLines = (side) => {
            if (!instanceCompareDialog.compareData) return [];

            const { instance1, instance2, differences } = instanceCompareDialog.compareData;
            const lines = [];
            let lineNumber = 1;

            if (side === 'left' || side === 'unified') {
                // 处理左侧或统一视图的数据
                const processNode = (node, level = 0) => {
                    const indent = '  '.repeat(level);
                    const content = `${indent}${node.nodeName} [${node.nodeType}]`;

                    // 检查节点是否有差异
                    let diffType = 'context';
                    let indicator = ' ';

                    if (differences.removed && differences.removed.some(r => r.nodeId === node.nodeId)) {
                        diffType = 'removed';
                        indicator = '-';
                    } else if (differences.modified && differences.modified.some(m => m.nodeId === node.nodeId)) {
                        diffType = 'modified';
                        indicator = '~';
                    }

                    lines.push({
                        lineNumber: lineNumber++,
                        content: content,
                        type: diffType,
                        indicator: indicator,
                        nodeId: node.nodeId
                    });

                    if (node.children) {
                        node.children.forEach(child => processNode(child, level + 1));
                    }
                };

                if (instance1.nodeTree) {
                    instance1.nodeTree.forEach(node => processNode(node));
                }
            }

            if (side === 'right') {
                // 处理右侧数据
                lineNumber = 1; // 重置行号
                const processNode = (node, level = 0) => {
                    const indent = '  '.repeat(level);
                    const content = `${indent}${node.nodeName} [${node.nodeType}]`;

                    // 检查节点是否有差异
                    let diffType = 'context';
                    let indicator = ' ';

                    if (differences.added && differences.added.some(a => a.nodeId === node.nodeId)) {
                        diffType = 'added';
                        indicator = '+';
                    } else if (differences.modified && differences.modified.some(m => m.nodeId === node.nodeId)) {
                        diffType = 'modified';
                        indicator = '~';
                    }

                    lines.push({
                        lineNumber: lineNumber++,
                        content: content,
                        type: diffType,
                        indicator: indicator,
                        nodeId: node.nodeId
                    });

                    if (node.children) {
                        node.children.forEach(child => processNode(child, level + 1));
                    }
                };

                if (instance2.nodeTree) {
                    instance2.nodeTree.forEach(node => processNode(node));
                }
            }

            // 如果只显示差异，过滤掉context行
            if (instanceCompareDialog.showOnlyDifferences) {
                return lines.filter(line => line.type !== 'context');
            }

            return lines;
        };

        // 获取差异行的CSS类
        const getDiffLineClass = (line) => {
            const classes = [];

            switch (line.type) {
                case 'added':
                    classes.push('diff-line-added');
                    break;
                case 'removed':
                    classes.push('diff-line-removed');
                    break;
                case 'modified':
                    classes.push('diff-line-modified');
                    break;
                case 'context':
                    classes.push('diff-line-context');
                    break;
                case 'separator':
                    classes.push('diff-line-separator');
                    break;
            }

            return classes.join(' ');
        };

        // 展开所有差异
        const expandAllDiffs = () => {
            ElMessage.info('展开所有差异');
        };

        // 折叠所有差异
        const collapseAllDiffs = () => {
            ElMessage.info('折叠所有差异');
        };

        // 提交实例
        const submitInstance = async () => {
            instanceDialog.loading = true;
            try {
                if (instanceDialog.isEdit) {
                    // 更新实例
                    await request('/api/v1/structure/instance/update_instance', {
                        method: 'POST',
                        body: JSON.stringify({
                            instanceId: instanceDialog.form.instanceId,
                            instanceName: instanceDialog.form.instanceName,
                            instanceDesc: instanceDialog.form.instanceDesc
                        })
                    });
                    ElMessage.success('更新成功');
                } else {
                    // 创建实例
                    await request('/api/v1/structure/instance/create_instance', {
                        method: 'POST',
                        body: JSON.stringify({
                            templateId: instanceDialog.form.templateId,
                            instanceCode: instanceDialog.form.instanceCode,
                            instanceName: instanceDialog.form.instanceName,
                            instanceDesc: instanceDialog.form.instanceDesc,
                            seriesId: instanceDialog.form.seriesId,
                            modelId: instanceDialog.form.modelId,
                            version: instanceDialog.form.instanceVersion,
                            creator: instanceDialog.form.creator
                        })
                    });
                    ElMessage.success('创建成功');
                }

                instanceDialog.visible = false;
                await queryInstances();
            } catch (error) {
                console.error('提交实例失败:', error);
                ElMessage.error('操作失败: ' + (error.message || '未知错误'));
            } finally {
                instanceDialog.loading = false;
            }
        };

        // 组件挂载时加载数据
        onMounted(() => {
            loadDashboardData();

            // 添加全局点击事件，用于隐藏右键菜单
            document.addEventListener('click', (e) => {
                // 如果点击的不是右键菜单本身，则隐藏菜单
                if (!e.target.closest('#contextMenu')) {
                    hideContextMenu();
                }
            });

            // 添加右键事件监听
            document.addEventListener('contextmenu', (e) => {
                // 如果不是在树节点上右键，则隐藏菜单
                if (!e.target.closest('.el-tree-node')) {
                    hideContextMenu();
                }
            });

            // 添加ESC键监听，用于关闭右键菜单
            document.addEventListener('keydown', (e) => {
                if (e.key === 'Escape') {
                    hideContextMenu();
                }
            });
        });

        // 组件卸载时清理事件监听
        onUnmounted(() => {
            document.removeEventListener('click', hideContextMenu);
            document.removeEventListener('contextmenu', hideContextMenu);
            document.removeEventListener('keydown', hideContextMenu);
        });

        // 备件管理相关数据和方法
        const partSearch = reactive({
            keyword: '',
            status: ''
        });
        const parts = ref([]);
        const partLoading = ref(false);

        // 备件对话框
        const partDialog = reactive({
            visible: false,
            isEdit: false,
            loading: false,
            form: {
                id: null,
                partCode: '',
                partName: '',
                creator: currentUser,
                remark: ''
            },
            rules: {
                partCode: [
                    { required: true, message: '请输入备件编码', trigger: 'blur' }
                ],
                partName: [
                    { required: true, message: '请输入备件名称', trigger: 'blur' }
                ],
                creator: [
                    { required: true, message: '请输入创建人', trigger: 'blur' }
                ]
            }
        });

        // 备件详情对话框
const partDetailDialog = reactive({
    visible: false,
    data: null,
    workHourTree: [] // 工时树数据
});

        // 批量绑定工时对话框
        const batchBindDialog = reactive({
            visible: false,
            loading: false,
            form: {
                file: null,
                creator: currentUser
            },
            rules: {
                creator: [
                    { required: true, message: '请输入创建人', trigger: 'blur' }
                ]
            }
        });

        // 上传模板对话框
        const uploadTemplateDialog = reactive({
            visible: false,
            loading: false,
            form: {
                file: null
            }
        });

        // 批量绑定结果对话框
        const batchBindResultDialog = reactive({
            visible: false,
            results: [],
            successCount: 0,
            failCount: 0
        });

        // 表单引用
        const partFormRef = ref(null);
        const batchBindFormRef = ref(null);
        const uploadTemplateFormRef = ref(null);

        // 刷新备件列表
        const refreshParts = async () => {
            partLoading.value = true;
            try {
                console.log('获取所有备件');
                const data = await request('/api/v1/part/list_parts');
                parts.value = data || [];
                console.log('获取备件列表成功，数量:', parts.value.length);
            } catch (error) {
                console.error('获取备件列表失败:', error);
                parts.value = [];
            } finally {
                partLoading.value = false;
            }
        };

        // 搜索备件
        const searchParts = async () => {
            partLoading.value = true;
            try {
                console.log('搜索备件，关键词:', partSearch.keyword, '状态:', partSearch.status);
                // 实际应该调用搜索API，这里暂时使用前端过滤模拟
                await refreshParts();
                
                if (partSearch.keyword || partSearch.status) {
                    const keyword = partSearch.keyword.toLowerCase();
                    parts.value = parts.value.filter(part => {
                        const matchKeyword = !keyword || 
                            part.partCode.toLowerCase().includes(keyword) || 
                            part.partName.toLowerCase().includes(keyword);
                            
                        const matchStatus = !partSearch.status || part.status === partSearch.status;
                        
                        return matchKeyword && matchStatus;
                    });
                }
            } catch (error) {
                console.error('搜索备件失败:', error);
                parts.value = [];
            } finally {
                partLoading.value = false;
            }
        };

        // 清空备件搜索条件
        const clearPartSearch = () => {
            partSearch.keyword = '';
            partSearch.status = '';
            refreshParts();
        };

        // 显示创建备件对话框
        const showCreatePartDialog = () => {
            partDialog.isEdit = false;
            Object.assign(partDialog.form, {
                id: null,
                partCode: '',
                partName: '',
                creator: currentUser,
                remark: ''
            });
            partDialog.visible = true;
            
            // 重置表单验证状态
            if (partFormRef.value) {
                partFormRef.value.clearValidate();
            }
        };

        // 编辑备件
        const editPart = (part) => {
            partDialog.isEdit = true;
            Object.assign(partDialog.form, {
                id: part.id,
                partCode: part.partCode,
                partName: part.partName,
                remark: part.remark
            });
            partDialog.visible = true;
            
            // 重置表单验证状态
            if (partFormRef.value) {
                partFormRef.value.clearValidate();
            }
        };

        // 提交备件表单
        const submitPart = async () => {
            // 表单验证
            if (!partFormRef.value) {
                console.error('备件表单引用未找到');
                return;
            }
            
            try {
                await partFormRef.value.validate();
            } catch (error) {
                console.log('备件表单验证失败:', error);
                return;
            }
            
            partDialog.loading = true;
            
            try {
                if (partDialog.isEdit) {
                    // 更新备件
                    console.log('更新备件:', partDialog.form);
                    await request('/api/v1/part/update_part', {
                        method: 'POST',
                        body: JSON.stringify({
                            id: partDialog.form.id,
                            partName: partDialog.form.partName,
                            remark: partDialog.form.remark
                        })
                    });
                    ElMessage.success('备件更新成功');
                } else {
                    // 创建备件
                    console.log('创建备件:', partDialog.form);
                    await request('/api/v1/part/create_part', {
                        method: 'POST',
                        body: JSON.stringify({
                            partCode: partDialog.form.partCode,
                            partName: partDialog.form.partName,
                            creator: partDialog.form.creator,
                            remark: partDialog.form.remark
                        })
                    });
                    ElMessage.success('备件创建成功');
                }
                
                // 关闭对话框
                partDialog.visible = false;
                
                // 刷新备件列表
                await refreshParts();
            } catch (error) {
                console.error('提交备件失败:', error);
                ElMessage.error('操作失败: ' + (error.message || '未知错误'));
            } finally {
                partDialog.loading = false;
            }
        };

        // 查看备件详情
const viewPartDetail = async (part) => {
    try {
        console.log('查看备件详情:', part.id);
        const response = await request(`/api/v1/part/get_part_detail?partId=${part.id}`);
        console.log('获取到的备件详情数据:', response);
        
        // 确保数据格式正确，处理API返回的数据结构
        let data = response.data || response;
        
        // 打印完整的数据结构，帮助调试
        console.log('完整的数据结构:', JSON.stringify(data));
        
        // 新的PartDetailVO结构包含partVO和workHourTreeVOList
        if (data && data.partVO) {
            // 设置备件详情数据
            partDetailDialog.data = data.partVO;
            
            // 设置工时树数据
            partDetailDialog.workHourTree = data.workHourTreeVOList || [];
            console.log('工时树数据:', partDetailDialog.workHourTree);
            
            // 处理工时树数据 - 直接使用WorkHourTreeVO结构
            if (partDetailDialog.workHourTree && partDetailDialog.workHourTree.length > 0) {
                console.log('原始工时树数据:', JSON.stringify(partDetailDialog.workHourTree));
                
                // 遍历每个工时树节点，确保数据结构完整
                partDetailDialog.workHourTree.forEach((item, index) => {
                    console.log(`工时树节点 ${index + 1}:`, item);
                    
                    // 确保workHourVO存在
                    if (!item.workHourVO) {
                        console.warn(`工时树节点 ${index + 1} 缺少workHourVO属性`);
                        item.workHourVO = {
                            id: item.id || 0,
                            code: item.code || 'UNKNOWN',
                            description: item.description || '未知工时',
                            standardHours: item.standardHours || 0,
                            type: item.type || 'UNKNOWN',
                            typeDescription: item.typeDescription || '未知类型'
                        };
                    } else {
                        console.log(`工时节点 ${index + 1} 的workHourVO:`, item.workHourVO);
                    }
                    
                    // 确保children是数组
                    if (item.children) {
                        if (!Array.isArray(item.children)) {
                            console.warn(`工时树节点 ${index + 1} 的children不是数组，进行转换`);
                            item.children = [item.children];
                        }
                        console.log(`工时节点 ${index + 1} 有 ${item.children.length} 个子工时`);
                        item.children.forEach((child, childIndex) => {
                            console.log(`子工时 ${childIndex + 1}:`, child);
                        });
                    } else {
                        console.log(`工时节点 ${index + 1} 没有子工时`);
                        item.children = [];
                    }
                });
                
                console.log('处理后的工时树数据:', partDetailDialog.workHourTree);
            }
        } else {
            // 如果数据不是预期的格式，尝试使用当前行数据填充
            console.warn('API返回的数据格式不正确，使用当前行数据');
            partDetailDialog.data = { ...part };
            partDetailDialog.workHourTree = [];
        }
        
        partDetailDialog.visible = true;
    } catch (error) {
        console.error('获取备件详情失败:', error);
        ElMessage.error('获取详情失败: ' + (error.message || '未知错误'));
    }
};

        // 切换备件状态（启用/禁用）
        const togglePartStatus = async (part) => {
            try {
                const action = part.status === 'ENABLED' ? '禁用' : '启用';
                const apiPath = part.status === 'ENABLED' ? '/api/v1/part/disable_part' : '/api/v1/part/enable_part';
                
                await ElMessageBox.confirm(
                    `确定要${action}备件"${part.partName}"吗？`,
                    `${action}确认`,
                    {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning'
                    }
                );
                
                console.log(`${action}备件:`, part.id);
                await request(apiPath, {
                    method: 'POST',
                    body: JSON.stringify({ partId: part.id })
                });
                
                ElMessage.success(`${action}成功`);
                await refreshParts();
            } catch (error) {
                if (error !== 'cancel') {
                    console.error('切换备件状态失败:', error);
                    ElMessage.error('操作失败: ' + (error.message || '未知错误'));
                }
            }
        };

        // 删除备件
        const deletePart = async (part) => {
            try {
                await ElMessageBox.confirm(
                    `确定要删除备件"${part.partName}"吗？此操作不可恢复！`,
                    '删除确认',
                    {
                        confirmButtonText: '确定删除',
                        cancelButtonText: '取消',
                        type: 'warning'
                    }
                );
                
                console.log('删除备件:', part.id);
                await request('/api/v1/part/delete_part', {
                    method: 'POST',
                    body: JSON.stringify({ partId: part.id })
                });
                
                ElMessage.success('删除成功');
                await refreshParts();
            } catch (error) {
                if (error !== 'cancel') {
                    console.error('删除备件失败:', error);
                    ElMessage.error('删除失败: ' + (error.message || '未知错误'));
                }
            }
        };

        // 显示批量绑定工时对话框
        const showBatchBindHoursDialog = () => {
            batchBindDialog.form = {
                file: null,
                creator: currentUser
            };
            batchBindDialog.visible = true;
            
            // 重置表单验证状态
            if (batchBindFormRef.value) {
                batchBindFormRef.value.clearValidate();
            }
        };

        // 处理批量绑定文件变化
        const handleBatchBindFileChange = (file) => {
            batchBindDialog.form.file = file.raw;
        };

        // 提交批量绑定
        const submitBatchBind = async () => {
            // 表单验证
            if (!batchBindFormRef.value) {
                console.error('批量绑定表单引用未找到');
                return;
            }
            
            try {
                await batchBindFormRef.value.validate();
            } catch (error) {
                console.log('批量绑定表单验证失败:', error);
                return;
            }
            
            if (!batchBindDialog.form.file) {
                ElMessage.error('请选择Excel文件');
                return;
            }
            
            batchBindDialog.loading = true;
            
            try {
                const formData = new FormData();
                formData.append('file', batchBindDialog.form.file);
                formData.append('creator', batchBindDialog.form.creator);
                
                const results = await request('/api/v1/part/batch_bind_hours', {
                    method: 'POST',
                    body: formData,
                    isFormData: true
                });
                
                // 计算成功和失败数量
                const successCount = results.filter(r => r.success).length;
                const failCount = results.length - successCount;
                
                // 显示结果对话框
                batchBindResultDialog.results = results;
                batchBindResultDialog.successCount = successCount;
                batchBindResultDialog.failCount = failCount;
                batchBindResultDialog.visible = true;
                
                // 关闭上传对话框
                batchBindDialog.visible = false;
            } catch (error) {
                console.error('批量绑定失败:', error);
                ElMessage.error('操作失败: ' + (error.message || '未知错误'));
            } finally {
                batchBindDialog.loading = false;
            }
        };

        // 下载工时模板
        const downloadPartHourTemplate = async () => {
            try {
                console.log('下载备件工时关联模板');
                
                // 使用fetch API获取文件并创建下载链接
                // 使用已定义的API_BASE作为基础URL
                const response = await fetch(`${API_BASE}/api/v1/part/download_template`, {
                    method: 'GET',
                    headers: {
                        'Accept': 'application/octet-stream'
                    }
                });
                
                if (!response.ok) {
                    throw new Error(`下载失败: ${response.status} ${response.statusText}`);
                }
                
                // 获取文件名
                const contentDisposition = response.headers.get('content-disposition');
                let filename = '备件工时关联模板.xlsx';
                if (contentDisposition) {
                    const filenameMatch = contentDisposition.match(/filename\*=UTF-8''([^;]+)/i);
                    if (filenameMatch && filenameMatch[1]) {
                        filename = decodeURIComponent(filenameMatch[1]);
                    }
                }
                
                // 创建Blob并下载
                const blob = await response.blob();
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = filename;
                document.body.appendChild(a);
                a.click();
                window.URL.revokeObjectURL(url);
                document.body.removeChild(a);
                
                console.log('模板下载成功');
                ElMessage.success('模板下载成功');
            } catch (error) {
                console.error('下载模板失败:', error);
                ElMessage.error('下载失败: ' + (error.message || '未知错误'));
            }
        };

        // 显示上传模板对话框
        const showUploadTemplateDialog = () => {
            uploadTemplateDialog.form = {
                file: null
            };
            uploadTemplateDialog.visible = true;
        };

        // 处理模板文件变化
        const handleTemplateFileChange = (file) => {
            uploadTemplateDialog.form.file = file.raw;
        };

        // 提交上传模板
        const submitUploadTemplate = async () => {
            if (!uploadTemplateDialog.form.file) {
                ElMessage.error('请选择模板文件');
                return;
            }
            
            uploadTemplateDialog.loading = true;
            
            try {
                const formData = new FormData();
                formData.append('file', uploadTemplateDialog.form.file);
                
                await request('/api/v1/part/upload_template', {
                    method: 'POST',
                    body: formData,
                    isFormData: true
                });
                
                uploadTemplateDialog.visible = false;
                ElMessage.success('模板上传成功');
            } catch (error) {
                console.error('上传模板失败:', error);
                ElMessage.error('上传失败: ' + (error.message || '未知错误'));
            } finally {
                uploadTemplateDialog.loading = false;
            }
        };

        // 解绑工时
        const unbindHour = async (partId, hourId) => {
            try {
                await ElMessageBox.confirm(
                    '确定要解绑此工时吗？',
                    '解绑确认',
                    {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning'
                    }
                );
                
                console.log('解绑工时:', partId, hourId);
                await request('/api/v1/part/unbind_hour', {
                    method: 'POST',
                    body: JSON.stringify({ partId, hourId })
                });
                
                ElMessage.success('解绑成功');
                
                // 更新详情对话框中的工时列表
                if (partDetailDialog.visible && partDetailDialog.data && partDetailDialog.data.id === partId) {
                    partDetailDialog.hours = partDetailDialog.hours.filter(h => h.id !== hourId);
                }
            } catch (error) {
                if (error !== 'cancel') {
                    console.error('解绑工时失败:', error);
                    ElMessage.error('解绑失败: ' + (error.message || '未知错误'));
                }
            }
        };

        // ==================== 工时管理 ====================
        // 工时搜索条件
        const workHourSearch = reactive({
            keyword: '',
            type: '',
            status: ''
        });

        // 工时列表数据
        const workHours = ref([]);
        const workHourLoading = ref(false);
        const expandedWorkHours = ref([]); // 当前展开的行IDs，空数组表示没有行被展开
        const subWorkHours = reactive({}); // 子工时数据，以父工时ID为键
        const subWorkHourLoading = reactive({}); // 子工时加载状态，以父工时ID为键

        // 创建/编辑主工时对话框
        const mainWorkHourDialog = reactive({
            visible: false,
            isEdit: false,
            loading: false,
            form: {
                code: '',
                description: '',
                standardHours: 1,
                type: 'MAIN',
                creator: ''
            },
            rules: {
                code: [
                    { required: true, message: '请输入工时编码', trigger: 'blur' }
                ],
                description: [
                    { required: true, message: '请输入工时描述', trigger: 'blur' }
                ],
                standardHours: [
                    { required: true, message: '请输入标准工时', trigger: 'blur' }
                ],
                type: [
                    { required: true, message: '请选择工时类型', trigger: 'change' }
                ],
                creator: [
                    { required: true, message: '请输入创建人', trigger: 'blur' }
                ]
            }
        });

        // 创建/编辑子工时对话框
        const subWorkHourDialog = reactive({
            visible: false,
            isEdit: false,
            loading: false,
            parentId: null,
            parentWorkHourName: '',
            form: {
                parentId: null,
                code: '',
                description: '',
                standardHours: 1,
                type: 'SUB',
                stepOrder: 1,
                creator: ''
            },
            rules: {
                code: [
                    { required: true, message: '请输入工时编码', trigger: 'blur' }
                ],
                description: [
                    { required: true, message: '请输入工时描述', trigger: 'blur' }
                ],
                standardHours: [
                    { required: true, message: '请输入标准工时', trigger: 'blur' }
                ],
                type: [
                    { required: true, message: '请选择工时类型', trigger: 'change' }
                ],
                stepOrder: [
                    { required: true, message: '请输入步骤顺序', trigger: 'blur' }
                ],
                creator: [
                    { required: true, message: '请输入创建人', trigger: 'blur' }
                ]
            }
        });

        // 工时详情对话框
        const workHourDetailDialog = reactive({
            visible: false,
            data: null,
            children: []
        });

        // 批量上传子工时对话框
        const batchUploadSubDialog = reactive({
            visible: false,
            loading: false,
            parentId: null,
            parentWorkHourName: '',
            form: {
                parentId: null,
                file: null,
                creator: ''
            },
            rules: {
                file: [
                    { required: true, message: '请选择Excel文件', trigger: 'change' }
                ],
                creator: [
                    { required: true, message: '请输入创建人', trigger: 'blur' }
                ]
            }
        });

        // 批量上传结果对话框
        const batchUploadResultDialog = reactive({
            visible: false,
            results: [],
            successCount: 0,
            failCount: 0
        });

        // 上传工时模板对话框
        const uploadWorkHourTemplateDialog = reactive({
            visible: false,
            loading: false,
            form: {
                file: null
            }
        });

        // 刷新工时列表
        const refreshWorkHours = async () => {
            try {
                workHourLoading.value = true;
                // 重置展开状态
                expandedWorkHours.value = [];
                const response = await request('/api/v1/workhour/query_main');
                workHours.value = response || [];
                console.log('获取工时列表成功，数量:', workHours.value.length);
            } catch (error) {
                console.error('获取工时列表失败:', error);
                ElMessage.error('获取工时列表失败: ' + (error.message || '未知错误'));
                workHours.value = [];
            } finally {
                workHourLoading.value = false;
            }
        };

        // 搜索工时
        const searchWorkHours = () => {
            console.log('搜索工时:', workHourSearch);
            refreshWorkHours();
            // TODO: 实现工时搜索功能
        };

        // 清空工时搜索条件
        const clearWorkHourSearch = () => {
            workHourSearch.keyword = '';
            workHourSearch.type = '';
            workHourSearch.status = '';
            refreshWorkHours();
        };

        // 处理工时展开事件
        const handleWorkHourExpand = async (row, expanded) => {
            console.log('展开/收起事件:', row.id, expanded);
            
            // 始终重置expandedWorkHours数组
            if (expanded) {
                // 如果是展开，则设置为当前行ID
                expandedWorkHours.value = [row.id];
                // 加载子工时数据
                await loadSubWorkHours(row.id);
            } else {
                // 如果是收起，则清空数组
                expandedWorkHours.value = [];
            }
            
            console.log('当前展开的行:', expandedWorkHours.value);
        };

        // 加载子工时
        const loadSubWorkHours = async (parentId, forceRefresh = false) => {
            try {
                // 如果已经有数据且不强制刷新，则直接返回
                if (!forceRefresh && subWorkHours[parentId] && subWorkHours[parentId].length > 0) {
                    console.log(`使用缓存的子工时数据，父工时ID: ${parentId}`);
                    return;
                }
                
                subWorkHourLoading[parentId] = true;
                const response = await request(`/api/v1/workhour/query_sub?parentId=${parentId}`);
                subWorkHours[parentId] = response || [];
                console.log(`获取子工时成功，父工时ID: ${parentId}, 数量:`, subWorkHours[parentId].length);
            } catch (error) {
                console.error(`获取子工时失败，父工时ID: ${parentId}:`, error);
                ElMessage.error('获取子工时失败: ' + (error.message || '未知错误'));
                subWorkHours[parentId] = [];
            } finally {
                subWorkHourLoading[parentId] = false;
            }
        };

        // 显示创建主工时对话框
        const showCreateMainWorkHourDialog = () => {
            mainWorkHourDialog.isEdit = false;
            mainWorkHourDialog.form = {
                code: '',
                description: '',
                standardHours: 1,
                type: 'MAIN',
                creator: currentUser.value
            };
            mainWorkHourDialog.visible = true;
        };

        // 显示创建子工时对话框
        const showCreateSubWorkHourDialog = (parentWorkHour) => {
            subWorkHourDialog.isEdit = false;
            subWorkHourDialog.parentId = parentWorkHour.id;
            subWorkHourDialog.parentWorkHourName = `${parentWorkHour.code} - ${parentWorkHour.description}`;
            subWorkHourDialog.form = {
                parentId: parentWorkHour.id,
                code: '',
                description: '',
                standardHours: 1,
                type: 'SUB',
                stepOrder: 1,
                creator: currentUser.value
            };
            subWorkHourDialog.visible = true;
        };

        // 提交主工时
        const submitMainWorkHour = async () => {
            try {
                mainWorkHourDialog.loading = true;
                
                if (mainWorkHourDialog.isEdit) {
                    // 更新主工时
                    const requestData = {
                        workHourId: mainWorkHourDialog.form.id,
                        description: mainWorkHourDialog.form.description,
                        standardHours: mainWorkHourDialog.form.standardHours,
                        stepOrder: null
                    };
                    
                    await request('/api/v1/workhour/update', {
                        method: 'POST',
                        body: JSON.stringify(requestData)
                    });
                    
                    ElMessage.success('更新工时成功');
                } else {
                    // 创建主工时
                    await request('/api/v1/workhour/create_main', {
                        method: 'POST',
                        body: JSON.stringify(mainWorkHourDialog.form)
                    });
                    
                    ElMessage.success('创建工时成功');
                }
                
                mainWorkHourDialog.visible = false;
                refreshWorkHours();
            } catch (error) {
                console.error('提交工时失败:', error);
                ElMessage.error('提交失败: ' + (error.message || '未知错误'));
            } finally {
                mainWorkHourDialog.loading = false;
            }
        };

        // 提交子工时
        const submitSubWorkHour = async () => {
            try {
                subWorkHourDialog.loading = true;
                
                if (subWorkHourDialog.isEdit) {
                    // 更新子工时
                    const requestData = {
                        workHourId: subWorkHourDialog.form.id,
                        description: subWorkHourDialog.form.description,
                        standardHours: subWorkHourDialog.form.standardHours,
                        stepOrder: subWorkHourDialog.form.stepOrder
                    };
                    
                    await request('/api/v1/workhour/update', {
                        method: 'POST',
                        body: JSON.stringify(requestData)
                    });
                    
                    ElMessage.success('更新工时成功');
                } else {
                    // 创建子工时
                    await request('/api/v1/workhour/create_sub', {
                        method: 'POST',
                        body: JSON.stringify(subWorkHourDialog.form)
                    });
                    
                    ElMessage.success('创建工时成功');
                }
                
                subWorkHourDialog.visible = false;
                await loadSubWorkHours(subWorkHourDialog.parentId);
            } catch (error) {
                console.error('提交工时失败:', error);
                ElMessage.error('提交失败: ' + (error.message || '未知错误'));
            } finally {
                subWorkHourDialog.loading = false;
            }
        };

        // 编辑工时
        const editWorkHour = (workHour) => {
            if (workHour.isMainWorkHour) {
                // 编辑主工时
                mainWorkHourDialog.isEdit = true;
                mainWorkHourDialog.form = {
                    id: workHour.id,
                    code: workHour.code,
                    description: workHour.description,
                    standardHours: workHour.standardHours,
                    type: workHour.type
                };
                mainWorkHourDialog.visible = true;
            } else {
                // 编辑子工时
                subWorkHourDialog.isEdit = true;
                subWorkHourDialog.parentId = workHour.parentId;
                subWorkHourDialog.form = {
                    id: workHour.id,
                    parentId: workHour.parentId,
                    code: workHour.code,
                    description: workHour.description,
                    standardHours: workHour.standardHours,
                    type: workHour.type,
                    stepOrder: workHour.stepOrder
                };
                subWorkHourDialog.visible = true;
            }
        };

        // 查看工时详情
        const viewWorkHourDetail = async (workHour) => {
            try {
                console.log('查看工时详情:', workHour.id);
                const response = await request(`/api/v1/workhour/detail?workHourId=${workHour.id}`);
                
                workHourDetailDialog.data = response;
                workHourDetailDialog.children = [];
                
                if (workHour.isMainWorkHour) {
                    // 加载子工时
                    try {
                        const subResponse = await request(`/api/v1/workhour/query_sub?parentId=${workHour.id}`);
                        workHourDetailDialog.children = subResponse || [];
                    } catch (error) {
                        console.error('获取子工时失败:', error);
                        workHourDetailDialog.children = [];
                    }
                }
                
                workHourDetailDialog.visible = true;
            } catch (error) {
                console.error('获取工时详情失败:', error);
                ElMessage.error('获取详情失败: ' + (error.message || '未知错误'));
            }
        };

        // 切换工时状态
        const toggleWorkHourStatus = async (workHour) => {
            try {
                const action = workHour.status === 'ENABLED' ? '禁用' : '启用';
                await ElMessageBox.confirm(
                    `确定要${action}该工时吗？`,
                    `${action}确认`,
                    {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning'
                    }
                );
                
                const url = workHour.status === 'ENABLED' ? 
                    `/api/v1/workhour/disable?workHourId=${workHour.id}` : 
                    `/api/v1/workhour/enable?workHourId=${workHour.id}`;
                
                await request(url, { method: 'POST' });
                
                // 立即更新本地状态
                workHour.status = workHour.status === 'ENABLED' ? 'DISABLED' : 'ENABLED';
                
                ElMessage.success(`${action}成功`);
                
                // 刷新数据，确保状态同步
                if (workHour.isMainWorkHour) {
                    // 如果是主工时，刷新整个列表
                    await refreshWorkHours();
                } else if (workHour.parentId) {
                    // 如果是子工时，刷新子工时列表
                    // 清除缓存，强制重新加载
                    delete subWorkHours[workHour.parentId];
                    await loadSubWorkHours(workHour.parentId);
                }
            } catch (error) {
                if (error !== 'cancel') {
                    console.error('切换工时状态失败:', error);
                    ElMessage.error('操作失败: ' + (error.message || '未知错误'));
                }
            }
        };

        // 删除工时
        const deleteWorkHour = async (workHour) => {
            try {
                await ElMessageBox.confirm(
                    '确定要删除该工时吗？',
                    '删除确认',
                    {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning'
                    }
                );
                
                await request(`/api/v1/workhour/delete?workHourId=${workHour.id}`, { method: 'POST' });
                
                ElMessage.success('删除成功');
                
                // 刷新数据，确保状态同步
                if (workHour.isMainWorkHour) {
                    // 如果是主工时，刷新整个列表
                    await refreshWorkHours();
                } else if (workHour.parentId) {
                    // 如果是子工时，刷新子工时列表
                    // 清除缓存，强制重新加载
                    delete subWorkHours[workHour.parentId];
                    await loadSubWorkHours(workHour.parentId);
                }
            } catch (error) {
                if (error !== 'cancel') {
                    console.error('删除工时失败:', error);
                    ElMessage.error('删除失败: ' + (error.message || '未知错误'));
                }
            }
        };

        // 显示批量上传子工时对话框
        const showBatchUploadSubWorkHoursDialog = (parentWorkHour) => {
            batchUploadSubDialog.parentId = parentWorkHour.id;
            batchUploadSubDialog.parentWorkHourName = `${parentWorkHour.code} - ${parentWorkHour.description}`;
            batchUploadSubDialog.form = {
                parentId: parentWorkHour.id,
                file: null,
                creator: currentUser.value
            };
            batchUploadSubDialog.visible = true;
        };

        // 处理批量上传文件变化
        const handleBatchUploadFileChange = (file) => {
            batchUploadSubDialog.form.file = file.raw;
        };

        // 提交批量上传子工时
        const submitBatchUploadSub = async () => {
            if (!batchUploadSubDialog.form.file) {
                ElMessage.error('请选择Excel文件');
                return;
            }
            
            if (!batchUploadSubDialog.form.creator) {
                ElMessage.error('请输入创建人');
                return;
            }
            
            batchUploadSubDialog.loading = true;
            
            try {
                const formData = new FormData();
                formData.append('file', batchUploadSubDialog.form.file);
                formData.append('parentId', batchUploadSubDialog.form.parentId);
                formData.append('creator', batchUploadSubDialog.form.creator);
                
                const response = await request('/api/v1/workhour/batch_upload_sub', {
                    method: 'POST',
                    body: formData,
                    isFormData: true
                });
                
                // 处理结果
                const results = response || [];
                const successCount = results.filter(item => item.success).length;
                const failCount = results.length - successCount;
                
                batchUploadResultDialog.results = results;
                batchUploadResultDialog.successCount = successCount;
                batchUploadResultDialog.failCount = failCount;
                batchUploadResultDialog.visible = true;
                
                batchUploadSubDialog.visible = false;
                
                // 刷新子工时列表
                if (successCount > 0) {
                    await loadSubWorkHours(batchUploadSubDialog.form.parentId);
                }
            } catch (error) {
                console.error('批量上传子工时失败:', error);
                ElMessage.error('上传失败: ' + (error.message || '未知错误'));
            } finally {
                batchUploadSubDialog.loading = false;
            }
        };

        // 下载工时模板
        const downloadWorkHourTemplate = async () => {
            try {
                console.log('下载工时批量上传模板');
                
                // 使用fetch API获取文件并创建下载链接
                const response = await fetch(`${API_BASE}/api/v1/workhour/download_template`, {
                    method: 'GET',
                    headers: {
                        'Accept': 'application/octet-stream'
                    }
                });
                
                if (!response.ok) {
                    throw new Error(`下载失败: ${response.status} ${response.statusText}`);
                }
                
                // 获取文件名
                const contentDisposition = response.headers.get('content-disposition');
                let filename = '工时批量上传模板.xlsx';
                if (contentDisposition) {
                    const filenameMatch = contentDisposition.match(/filename\*=UTF-8''([^;]+)/i);
                    if (filenameMatch && filenameMatch[1]) {
                        filename = decodeURIComponent(filenameMatch[1]);
                    }
                }
                
                // 创建Blob并下载
                const blob = await response.blob();
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = filename;
                document.body.appendChild(a);
                a.click();
                window.URL.revokeObjectURL(url);
                document.body.removeChild(a);
                
                console.log('模板下载成功');
                ElMessage.success('模板下载成功');
            } catch (error) {
                console.error('下载模板失败:', error);
                ElMessage.error('下载失败: ' + (error.message || '未知错误'));
            }
        };

        // 显示上传工时模板对话框
        const showUploadWorkHourTemplateDialog = () => {
            uploadWorkHourTemplateDialog.form = {
                file: null
            };
            uploadWorkHourTemplateDialog.visible = true;
        };

        // 处理工时模板文件变化
        const handleWorkHourTemplateFileChange = (file) => {
            uploadWorkHourTemplateDialog.form.file = file.raw;
        };

        // 提交上传工时模板
        const submitUploadWorkHourTemplate = async () => {
            if (!uploadWorkHourTemplateDialog.form.file) {
                ElMessage.error('请选择模板文件');
                return;
            }
            
            uploadWorkHourTemplateDialog.loading = true;
            
            try {
                const formData = new FormData();
                formData.append('file', uploadWorkHourTemplateDialog.form.file);
                
                await request('/api/v1/workhour/upload_template', {
                    method: 'POST',
                    body: formData,
                    isFormData: true
                });
                
                uploadWorkHourTemplateDialog.visible = false;
                ElMessage.success('模板上传成功');
            } catch (error) {
                console.error('上传模板失败:', error);
                ElMessage.error('上传失败: ' + (error.message || '未知错误'));
            } finally {
                uploadWorkHourTemplateDialog.loading = false;
            }
        };

        return {
            // 响应式数据
            activeMenu,
            currentUser,
            stats,
            categories,
            categoryLoading,
            categorySearch,
            configItems,
            itemLoading,
            selectedCategoryId,
            itemSearch,
            usages,
            usageLoading,
            usageQuery,
            systemGroups,
            allConfigItems,
            categoryDialog,
            itemDialog,
            usageDialog,

            // 模板和实例数据
            templates,
            templateLoading,
            templateQuery,
            instances,
            instanceLoading,
            instanceQuery,
            availableTemplates,
            availableInstances,
            templateDialog,
            templateNodeDialog,
            templateDetailDialog,
            instanceDialog,
            instanceDetailDialog,
            instanceCompareDialog,
            contextMenu,
            nodeDialog,

            // 树形结构配置
            templateTreeProps,
            instanceTreeProps,

            // 工具方法
            getNodeTypeText,
            getNodeTypeColor,
            formatDateTime,
            formatStatus,
            getIdPlaceholder,

            // 方法
            handleMenuSelect,
            loadDashboardData,
            loadCategories,
            searchCategories,
            clearSearch,
            refreshCategories,
            showCreateCategoryDialog,
            editCategory,
            submitCategory,
            deleteCategory,
            selectCategory,
            loadConfigItems,
            showCreateItemDialog,
            editItem,
            submitItem,
            deleteItem,
            searchItems,
            clearItemSearch,

            // 用法管理方法
            usages,
            usageLoading,
            usageQuery,
            usageDialog,
            usageDetailDialog,
            imageDialog,
            queryUsages,
            clearUsageQuery,
            showCreateUsageDialog,
            editUsage,
            submitUsage,
            viewUsageDetail,
            viewExplodedImage,
            deleteUsage,
            restoreUsage,
            handleFileChange,
            addCombination,
            removeCombination,
            loadConfigItemsForCategory,
            removeConfigItem,
            getConfigItemName,
            viewCombinationDetails,
            deleteCombination,
            
            // 用法备件关联方法
            relatedPartsLoading,
            combinationsLoading,
            batchUploadResultDialog,
            loadRelatedParts,
            searchRelatedParts,
            unbindPart,
            clearAllParts,
            downloadPartTemplate,
            beforePartUpload,
            uploadPartRelations,

            // 模板管理方法
            queryTemplates,
            clearTemplateQuery,
            showCreateTemplateDialog,
            editTemplate,
            copyTemplate,
            toggleTemplateStatus,
            viewTemplateDetail,
            submitTemplate,
            addTemplateNode,
            addChildTemplateNode,
            editTemplateNode,
            deleteTemplateNode,
            submitTemplateNode,
            expandAllTemplateNodes,
            collapseAllTemplateNodes,
            allowTemplateDrop,
            handleTemplateNodeDrop,

            // 实例管理方法
            queryInstances,
            clearInstanceQuery,
            showCreateInstanceDialog,
            onTemplateChange,
            copyInstance,
            toggleInstancePublish,
            toggleInstanceStatus,
            viewInstanceDetail,
            compareInstance,
            loadCompareData,
            getFormattedDiffLines,
            getDiffLineClass,
            expandAllDiffs,
            collapseAllDiffs,
            submitInstance,

            // 右键菜单方法
            hideContextMenu,
            handleTemplateNodeContextMenu,
            handleInstanceNodeContextMenu,
            handleInstanceNodeClick,
            addChildNode,
            editCurrentNode,
            deleteCurrentNode,
            moveNodeUp,
            moveNodeDown,
            addTemplateRootNode,
            addInstanceRootNode,
            expandAllInstanceNodes,
            collapseAllInstanceNodes,
            showAddNodeDialog,
            submitNode,
            getAllTreeNodes,
            moveNodeToServer,
            refreshTreeData,

            logout,

            // 表单引用
            categoryFormRef,
            itemFormRef,
            usageFormRef,

            // 树组件引用
            templateTreeRef,
            instanceTreeRef,

            // 备件管理相关数据和方法
            partSearch,
            parts,
            partLoading,
            partDialog,
            partDetailDialog,
            batchBindDialog,
            batchBindResultDialog,
            partFormRef,
            batchBindFormRef,
            uploadTemplateDialog,
            uploadTemplateFormRef,
            refreshParts,
            searchParts,
            clearPartSearch,
            showCreatePartDialog,
            editPart,
            submitPart,
            viewPartDetail,
            togglePartStatus,
            deletePart,
            showBatchBindHoursDialog,
            handleBatchBindFileChange,
            submitBatchBind,
            downloadPartHourTemplate,
            showUploadTemplateDialog,
            handleTemplateFileChange,
            submitUploadTemplate,
            unbindHour,

            // 工时管理相关数据和方法
            workHourSearch,
            workHours,
            workHourLoading,
            expandedWorkHours,
            subWorkHours,
            subWorkHourLoading,
            mainWorkHourDialog,
            subWorkHourDialog,
            workHourDetailDialog,
            batchUploadSubDialog,
            batchUploadResultDialog,
            uploadWorkHourTemplateDialog,
            refreshWorkHours,
            searchWorkHours,
            clearWorkHourSearch,
            handleWorkHourExpand,
            loadSubWorkHours,
            showCreateMainWorkHourDialog,
            showCreateSubWorkHourDialog,
            submitMainWorkHour,
            submitSubWorkHour,
            editWorkHour,
            viewWorkHourDetail,
            toggleWorkHourStatus,
            deleteWorkHour,
            showBatchUploadSubWorkHoursDialog,
            handleBatchUploadFileChange,
            submitBatchUploadSub,
            downloadWorkHourTemplate,
            showUploadWorkHourTemplateDialog,
            handleWorkHourTemplateFileChange,
            submitUploadWorkHourTemplate
        };
    }
});

// 使用 Element Plus
app.use(ElementPlus);

// 挂载应用
app.mount('#app');
