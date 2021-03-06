
import AbstractApi from './../AbstractApi'

import requestGroupImpi from './../RequestGroupImpi'


class TestApi extends AbstractApi {

    /**
        * 
     *
        * <div class='http-info'>http 说明：<b>Api Url:</b> <b>testVoid</b>
    * <ul>
    * <li><b>Model:</b> void</li>
    * </ul>
    * </div>

     */
    testVoid(){
        let _path = null;
        return super._request("testApi", "POST", "testVoid", _path, null);
    }


    /**
        * 
     *
        * <div class='http-info'>http 说明：<b>Api Url:</b> <b>testObjectList</b>
    * <ul>
    * <li><b>Form:</b>TestFormtestObjectList</li>
    * <li><b>Model:</b> TestObjectList</li>
    * </ul>
    * </div>
    * @see TestObjectList
    * @see TestForm

     */
    testObjectList(testForm){
        let _path = null;
        return super._request("testApi", "POST", "testObjectList", _path, testForm);
    }


    /**
        * 添加
     *
        * <div class='http-info'>http 说明：<b>Api Url:</b> <b>test</b>
    * <ul>
    * <li><b>Form:</b>TestFormcreate</li>
    * <li><b>Model:</b> TestObject</li>
    * </ul>
    * </div>
    * @see TestObject
    * @see TestForm

     */
    create(testForm){
        let _path = null;
        return super._request("testApi", "POST", "test", _path, testForm);
    }


    /**
        * 
     *
        * <div class='http-info'>http 说明：<b>Api Url:</b> <b>test/{id}</b>
    * <ul>
    * <li><b>PathVariable:</b> string id</li>
    * <li><b>Model:</b> TestObject</li>
    * </ul>
    * </div>
    * @see TestObject
    * @see string

     */
    get(id){
        let _path = {};
        _path["id"] = id;
        return super._request("testApi", "GET", "test/{id}", _path, null);
    }


    /**
        * 
     *
        * <div class='http-info'>http 说明：<b>Api Url:</b> <b>test</b>
    * <ul>
    * <li><b>Form:</b>TestFormupdate</li>
    * <li><b>Model:</b> TestObject</li>
    * </ul>
    * </div>
    * @see TestObject
    * @see TestForm

     */
    update(testForm){
        let _path = null;
        return super._request("testApi", "PUT", "test", _path, testForm);
    }


    /**
        * 
     *
        * <div class='http-info'>http 说明：<b>Api Url:</b> <b>test</b>
    * <ul>
    * <li><b>Form:</b>TestFormpatchUpdate</li>
    * <li><b>Model:</b> TestObject</li>
    * </ul>
    * </div>
    * @see TestObject
    * @see TestForm

     */
    patchUpdate(testForm){
        let _path = null;
        return super._request("testApi", "PATCH", "test", _path, testForm);
    }


    /**
        * 
     *
        * <div class='http-info'>http 说明：<b>Api Url:</b> <b>test/{id}</b>
    * <ul>
    * <li><b>PathVariable:</b> string id</li>
    * <li><b>Model:</b> boolean</li>
    * </ul>
    * </div>
    * @see boolean
    * @see string

     */
    delete(id){
        let _path = {};
        _path["id"] = id;
        return super._request("testApi", "DELETE", "test/{id}", _path, null);
    }


    /**
        * 
     *
        * <div class='http-info'>http 说明：<b>Api Url:</b> <b>tests/{id}</b>
    * <ul>
    * <li><b>PathVariable:</b> string[] id</li>
    * <li><b>Model:</b> number</li>
    * </ul>
    * </div>
    * @see number
    * @see string[]

     */
    deletes(id){
        let _path = {};
        _path["id"] = id;
        return super._request("testApi", "DELETE", "tests/{id}", _path, null);
    }


    /**
        * 
     *
        * <div class='http-info'>http 说明：<b>Api Url:</b> <b>search/{id}/{name}</b>
    * <ul>
    * <li><b>PathVariable:</b> string id</li>
    * <li><b>PathVariable:</b> string name</li>
    * <li><b>Model:</b> TestObject</li>
    * </ul>
    * </div>
    * @see TestObject
    * @see string
    * @see string

     */
    search(id, name){
        let _path = {};
        _path["id"] = id;
        _path["name"] = name;
        return super._request("testApi", "GET", "search/{id}/{name}", _path, null);
    }


    /**
        * 
     *
        * <div class='http-info'>http 说明：<b>Api Url:</b> <b>testString/{name}</b>
    * <ul>
    * <li><b>PathVariable:</b> string name</li>
    * <li><b>Model:</b> string</li>
    * </ul>
    * </div>
    * @see string
    * @see string

     */
    testString(name){
        let _path = {};
        _path["name"] = name;
        return super._request("testApi", "GET", "testString/{name}", _path, null);
    }


    /**
        * 
     *
        * <div class='http-info'>http 说明：<b>Api Url:</b> <b>testString1/{name}/{age}</b>
    * <ul>
    * <li><b>PathVariable:</b> string name</li>
    * <li><b>PathVariable:</b> string age</li>
    * <li><b>Model:</b> string</li>
    * </ul>
    * </div>
    * @see string
    * @see string
    * @see string

     */
    testString1(name, age){
        let _path = {};
        _path["name"] = name;
        _path["age"] = age;
        return super._request("testApi", "GET", "testString1/{name}/{age}", _path, null);
    }

}

export { TestApi };
const testApi = new TestApi();
testApi._init(requestGroupImpi);
export default testApi;

