package org.forkjoin.apikit.generator;

import com.google.common.base.Joiner;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.forkjoin.apikit.AbstractGenerator;
import org.forkjoin.apikit.AnalyseException;
import org.forkjoin.apikit.generator.apidoc.ApiDocApi;
import org.forkjoin.apikit.generator.apidoc.ApiDocProject;
import org.forkjoin.apikit.info.*;
import org.forkjoin.apikit.spring.utils.JsonUtils;
import org.forkjoin.apikit.utils.CommentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 生成 apidoc 项目规定的格式的json
 * 标准 https://github.com/apidoc/apidoc-spec
 */
public class ApiDocGenerator extends AbstractGenerator {
    private static final Logger log = LoggerFactory.getLogger(ApiDocGenerator.class);

    public static final String API_PROJECT = "api_project";
    public static final String API_DATA = "api_data";
    public static final String JSON_SUFFIX = ".json";
    public static final String JS_SUFFIX = ".js";

    private List<ApiDocApi> apis = new ArrayList<>();
    private ApiDocProject apiDocProject = new ApiDocProject();
    private boolean isAmd = false;

    public ApiDocGenerator() {

    }

    public ApiDocProject getApiDocProject() {
        return apiDocProject;
    }

    public void setApiDocProject(ApiDocProject apiDocProject) {
        this.apiDocProject = apiDocProject;
    }

    @Override
    public void generateTool() throws Exception {
        String suffix = isAmd ? JS_SUFFIX : JSON_SUFFIX;

        File apiProjectFile = new File(outPath, API_PROJECT + suffix);
        apiDocProject.setVersion(getVersion());
        apiDocProject.setTemplate(new ApiDocProject.Template(true, true));
        apiDocProject.setGenerator(new ApiDocProject.Generator("ApiKit 生成器生成，当前api版本:" + getVersion(), new Date()));
        String apiDocProjectJson = JsonUtils.serialize(apiDocProject);
        if (isAmd) {
            apiDocProjectJson = "define(" + apiDocProjectJson + ");";
        }
        FileUtils.write(apiProjectFile, apiDocProjectJson);

        File apiDataFile = new File(outPath, API_DATA + suffix);
        String apiDataJson = JsonUtils.serialize(apis);
        if (isAmd) {
            apiDataJson = "define({ api:" + apiDataJson + "});";
        }
        FileUtils.write(apiDataFile, apiDataJson);
    }


    @Override
    public void generateApi(ApiInfo apiInfo) throws Exception {
        apiInfo.getMethodInfos().forEach(r -> generateApiMethod(apiInfo, r));
    }

    protected void generateApiMethod(ApiInfo apiInfo, ApiMethodInfo methodInfo) {
        ApiDocApi api = new ApiDocApi();
        api.setName(methodInfo.getName());
        api.setDescription(transform(methodInfo.getComment()));
        api.setGroup(apiInfo.getName());
        api.setType(methodInfo.getType().name().toLowerCase());
        api.setUrl(methodInfo.getUrl());
        api.setVersion(this.getVersion());
        api.setFilename(apiInfo.getFullName());
        api.setTitle(getCommentTitle(methodInfo.getComment()));

        if (methodInfo.isAccount()) {
            api.setPermission(new ApiDocApi.Permission("user", "需要登录", "需要登录后调用"));
        } else {
            api.setPermission(new ApiDocApi.Permission("anonymous", "不用登录", ""));
        }


        //参数
        ApiDocApi.Fields parameter = new ApiDocApi.Fields();
        parameter.add("Parameter", transformParameter(apiInfo, methodInfo));
        api.setParameter(parameter);

        ApiDocApi.Fields success = new ApiDocApi.Fields();
        success.add("Success 200", transformResult("Success 200", apiInfo, methodInfo));
        api.setSuccess(success);
        apis.add(api);
    }

    private List<ApiDocApi.Field> transformResult(String groupName, ApiInfo apiInfo, ApiMethodInfo methodInfo) {
        TypeInfo resultWrappedType = methodInfo.getResultWrappedType();

        List<ApiDocApi.Field> list = new ArrayList<>();
        list.add(new ApiDocApi.Field(groupName, "int", "status", false, "状态,0表示成功,其他表示错误，请查看协议公共部分", "0"));

        list.add(new ApiDocApi.Field(groupName, "String", "msg", true, "错误消息,表示错误的详细信息，支持国际化", null));
        list.add(new ApiDocApi.Field(groupName, "Map", "msgMap", true, "详细错误消息,key:表示错误字段,value:表示错误信息", null));

        if (resultWrappedType.getFullName().equals("org.forkjoin.apikit.core.PageResult")) {
            list.add(new ApiDocApi.Field(groupName, "int", "count", true, "记录总数", "0"));
            list.add(new ApiDocApi.Field(groupName, "int", "page", true, "当前页,1开始", "0"));
            list.add(new ApiDocApi.Field(groupName, "int", "pageSize", true, "一页大小", "0"));
        }

        TypeInfo resultDataType = methodInfo.getResultDataType();
        if (!resultDataType.getType().equals(TypeInfo.Type.VOID)) {
            if (!resultDataType.getType().isBaseType()) {

                list.addAll(getFields("data", groupName, resultDataType));
            } else {
                JavadocInfo comment = methodInfo.getComment();
                String description = "协议返回数据";
                if (comment != null) {
                    List<String> returnComment = comment.get("@return");
                    description = Joiner.on("\n").join(returnComment);
                }
                list.add(new ApiDocApi.Field(
                        groupName, resultDataType.getType().getPrimitiveName(),
                        "data", true, description, null
                ));
            }
        }
        return list;
    }

    private List<ApiDocApi.Field> transformParameter(ApiInfo apiInfo, ApiMethodInfo methodInfo) {
        ArrayList<ApiMethodParamInfo> formParams = methodInfo.getFormParams();
        return formParams.stream()
                .map(p -> getFields(null, "Parameter", p.getTypeInfo()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     *
     */
    private List<ApiDocApi.Field> getFields(String prefix, String groupName, TypeInfo type) {
        ArrayList<ApiDocApi.Field> list = new ArrayList<>();
        getFields(list, prefix, groupName, type, type.getTypeArguments(), 0);
        return list;
    }

    private void getFields(List<ApiDocApi.Field> list, String prefix, String groupName, TypeInfo type, List<TypeInfo> actualTypeArguments, int level) {
        MessageInfo message = context.getMessage(type);
        if (message == null) {
            throw new AnalyseException("找不到类型对应的消息:" + type);
        }
        if (level > 2) {
            log.info("超出解析最大层级！");
            return;
        }
        message.getProperties().forEach(propertyInfo -> {
            String name = StringUtils.isEmpty(prefix) ? propertyInfo.getName() : prefix + "." + propertyInfo.getName();
            TypeInfo propertyTypeInfo = propertyInfo.getTypeInfo();
            if (propertyTypeInfo.isOtherType() && propertyInfo.getTypeInfo().isInside()) {
                if (propertyTypeInfo.isGeneric()) {
                    /*
                     * 计算实际参数的类型
                     */
                    int i = message.getTypeParameters().indexOf(propertyTypeInfo.getName());
                    //不能找到实际参数，那么是object类型，不处理
                    if (i < actualTypeArguments.size()) {
                        TypeInfo typeInfo = actualTypeArguments.get(i);
                        getFields(list, name, groupName, typeInfo, typeInfo.getTypeArguments(), level + 1);
                    } else {
                        log.info("不能找到实际参数[name:{},prefix:{},groupName:{},type:[],propertyInfo:{}", propertyTypeInfo.getName(), prefix, groupName, type, propertyInfo);
                    }
                } else {
                    List<TypeInfo> typeArguments = propertyTypeInfo.getTypeArguments();

                    List<TypeInfo> childActualTypeArguments = typeArguments
                            .stream()
                            .map(r -> {
                                if (r.isGeneric()) {
                                    int i = message.getTypeParameters().indexOf(r.getName());
                                    if (i < type.getTypeArguments().size()) {
                                        return actualTypeArguments.get(i);
                                    } else {
                                        log.info("不能找到实际参数[name:{},prefix:{},groupName:{},type:[],propertyInfo:{}", r.getName(), prefix, groupName, type, propertyInfo);
                                    }
                                }
                                return r;
                            }).collect(Collectors.toList());

                    getFields(list, name, groupName, propertyTypeInfo, childActualTypeArguments, level + 1);
                }

            } else {
                ApiDocApi.Field field = new ApiDocApi.Field();

                field.setGroup(groupName);
                field.setDescription(transform(propertyInfo.getComment()));
                field.setField(name);

                boolean notNull = propertyInfo.getAnnotations().stream().anyMatch(
                        r -> r.getTypeInfo().getFullName().equals("javax.validation.constraints.NotNull")
                );
                field.setOptional(!notNull);
                field.setType(propertyTypeInfo.getType().getPrimitiveName());
                //TODO 处理默认值
                list.add(field);
            }
        });
    }

    @Override
    public void generateMessage(MessageInfo messageInfo) throws Exception {

    }


    private String transform(JavadocInfo comment) {
        return CommentUtils.formatComment(comment, "");
    }

    private String getCommentTitle(JavadocInfo comment) {
        String s = CommentUtils.formatComment(comment, "");
        if (s != null) {
            return s.trim().split("\n")[0];
        } else {
            return "";
        }
    }

    public boolean isAmd() {
        return isAmd;
    }

    public void setAmd(boolean amd) {
        isAmd = amd;
    }

    List<ApiDocApi> getApis() {
        return apis;
    }
}