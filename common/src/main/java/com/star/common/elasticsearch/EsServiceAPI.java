package com.star.common.elasticsearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.admin.indices.alias.exists.AliasesExistResponse;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.swjoy.elasticsearch.bean.EsParam;
import com.swjoy.elasticsearch.bean.QueryResult;
import com.swjoy.elasticsearch.client.EsClientPool;
import com.swjoy.elasticsearch.constants.Duration;
import com.swjoy.elasticsearch.constants.StatusConstants;
import com.swjoy.elasticsearch.constants.VideoTypeEnum;

/**
* @author 作者 : star
* @version 创建时间：2017年3月28日 上午11:28:24
* 类说明 : Elasticsearch java api 基本使用之增、删、改、查
*/
@Component
public class EsServiceAPI {
	private static Logger logger = LoggerFactory.getLogger(EsServiceAPI.class);
    
    /**
     * 创建索引节点
     * @param client
     * @param index
     */
    public void createIndex(String index) {
    	TransportClient client = null;
    	try {
			// 创建index库
    		EsClientPool newEsClient = EsClientPool.getInstance();
    		client = newEsClient.getTransPortClient();
    		Map<String, Object> settings = new HashMap<String, Object>(2);
    		settings.put("number_of_shards", 2); // 设置两个分片，默认是5个
    		settings.put("number_of_replicas", 1);// 设置一个数据副本
			client.admin().indices().prepareCreate(index).setSettings(settings).execute().actionGet();
		} catch (Exception e) {
			logger.error("index={}创建index库失败！", index, e);
			throw new RuntimeException(e);
		} finally {
			if (client != null) {
				EsClientPool.getInstance().releaseClient(client);
			}
		}
	}
    
    /**
     * 删除索引节点
     * @param client
     * @param index
     */
    public void deleteIndex(String index) {
    	TransportClient client = null;
    	try {
			// 删除index库
    		EsClientPool newEsClient = EsClientPool.getInstance();
    		client = newEsClient.getTransPortClient();
    		client.admin().indices().prepareDelete(index).execute().actionGet();
		} catch (Exception e) {
			logger.error("index={}删除index库失败！", index, e);
			throw new RuntimeException(e);
		} finally {
			if (client != null) {
				EsClientPool.getInstance().releaseClient(client);
			}
		}
	}
    
    /**
     * 判断该索引节点是否已经存在
     * @param indexName
     * @return
     */
    public boolean isExistsIndex(String index) {
    	TransportClient client = null;
        try {
        	EsClientPool newEsClient = EsClientPool.getInstance();
    		client = newEsClient.getTransPortClient();
			IndicesExistsResponse response = client
			        .admin()
			        .indices()
			        .exists(new IndicesExistsRequest(index))
			        .actionGet();
			return response.isExists();
		} catch (Exception e) {
			logger.error("index={}判断该索引节点是否已经存在！", index, e);
			return false;
		} finally {
			if (client != null) {
				EsClientPool.getInstance().releaseClient(client);
			}
		}
    }
    
    /**
     * 创建索引节点别名
     * @param client
     * @param index
     */
    public void createIndexAlias(String index, String aliase) {
    	TransportClient client = null;
    	try {
    		EsClientPool newEsClient = EsClientPool.getInstance();
    		client = newEsClient.getTransPortClient();
			// 创建索引节点别名
    		client.admin().indices().prepareAliases().addAlias(index, aliase).execute().actionGet();
		} catch (Exception e) {
			logger.error("index and alias={}创建索引节点别名！", index+"::"+aliase, e);
			throw new RuntimeException(e);
		} finally {
			if (client != null) {
				EsClientPool.getInstance().releaseClient(client);
			}
		}
	}
    
    /**
     * 判断该别名是否存在索引节点别名
     * @param client
     * @param alias
     */
    public boolean isExistsAlias(String alias) {
    	TransportClient client = null;
    	try {
    		EsClientPool newEsClient = EsClientPool.getInstance();
    		client = newEsClient.getTransPortClient();
			// 创建索引节点别名
    		AliasesExistResponse response = client
                    .admin()
                    .indices()
                    .aliasesExist(new GetAliasesRequest(alias))
                    .actionGet();
            return response.isExists();
		} catch (Exception e) {
			logger.error("index and alias={}判断该别名是否存在索引节点别名失败！", alias, e);
			return false;
		} finally {
			if (client != null) {
				EsClientPool.getInstance().releaseClient(client);
			}
		}
	}
    
    /**
     * 创建mapping
     * @param client
     * @param index
     * @param indexType
     */
    public void createMapping(String index, String indexType) {
    	TransportClient client = null;
    	XContentBuilder mapping = null;
        try {
        	mapping = XContentFactory.jsonBuilder()
        	.startObject()
        	.startObject(indexType)
        	.startObject("properties")
            .startObject("id").field("type", "integer").field("index","not_analyzed").endObject()
            .startObject("cpSourceId").field("type", "string").field("index","not_analyzed").endObject()
            .startObject("companyId").field("type", "integer").field("index","not_analyzed").endObject()
            .startObject("videoName").field("type", "string").field("analyzer", "ik_smart").endObject()
            .startObject("videoTitle").field("type", "string").field("analyzer", "ik_smart").endObject()
            .startObject("videoNum").field("type", "integer").field("index","no").endObject()
            .startObject("videoType").field("type", "integer").field("index","not_analyzed").endObject()
            
            .startObject("videoArea")
            .field("type","string")
            .field("analyzer", "ik_smart")
        	.startObject("fields")
        		.startObject("noanVideoArea")
    				.field("type", "string")
    				.field("index","not_analyzed")
    			.endObject()
        	.endObject()
            .endObject()
            
            .startObject("videoYear").field("type", "integer").field("index","not_analyzed").endObject()
            .startObject("videoThumbimg").field("type", "string").field("index","not_analyzed").endObject() // 横图，小图
            .startObject("videoBigimg").field("type", "string").field("index","not_analyzed").endObject()  // 竖图，大图
            .startObject("videoTag").field("type", "string").field("analyzer", "ik_smart").endObject()
            .startObject("videoDesc").field("type", "string").field("index","no").endObject()
            .startObject("videoScore").field("type", "double").endObject()
            .startObject("videoDuration").field("type", "integer").endObject()
            .startObject("videoPubDate").field("type", "string").endObject()
            .startObject("videoActor").field("type", "string").field("analyzer", "ik_smart").endObject()
            .startObject("videoDirector").field("type", "string").field("analyzer", "ik_smart").endObject()
            .startObject("updateTime").field("type", "long").endObject()
            .startObject("hot").field("type", "double").field("index","not_analyzed").endObject()
            .startObject("multiPlayUrl").field("type", "string").field("index","no").field("doc_values","false").endObject()
            .startObject("status").field("type", "short").field("index","not_analyzed").endObject()
            .startObject("startTime").field("type", "long").field("index","not_analyzed").endObject()
            .startObject("endTime").field("type", "long").field("index","not_analyzed").endObject()
            .startObject("isBuildHtml").field("type", "byte").field("index","no").endObject()
            
            .endObject()
            .endObject()
			.endObject();
        	EsClientPool newEsClient = EsClientPool.getInstance();
    		client = newEsClient.getTransPortClient();
            PutMappingRequestBuilder builder = client.admin().indices().preparePutMapping(index);
            builder.setType(indexType);
            builder.setSource(mapping);
            builder.execute().actionGet();
        } catch (IOException e) {
        	e.printStackTrace();
        	logger.error("创建mapping失败！", e);
        } finally {
			if (client != null) {
				EsClientPool.getInstance().releaseClient(client);
			}
		}
    	
    }
    
    /**
	 * 更新某条索引的某个字段的数据
	 * @param client
	 * @param index 索引库名称（相当于数据库概念）
	 * @param indexType 类型名称 （相当于表的概念
	 * @param field 字段名
	 * @param value 值
	 * @param id 主键ID
	 */
	public void updateIndexSingleField(String index, String indexType
			, String field, Object value, String id) {
		TransportClient client = null;
		if (Strings.isNullOrEmpty(field) || value == null) {
			return;
		}
		try {
			EsClientPool newEsClient = EsClientPool.getInstance();
    		client = newEsClient.getTransPortClient();
			client.prepareUpdate(index, indexType, id).setRetryOnConflict(5)
		    .setDoc(XContentFactory.jsonBuilder()       
		        .startObject()
		        .field(field, value)
		        .endObject()).get();
		} catch (Exception e) {
			logger.error("fieldAndValue={}, 更新单个字段数据失败!", field + ":::" +value, e);
		} finally {
			if (client != null) {
				EsClientPool.getInstance().releaseClient(client);
			}
		}
	}
	
	public void updateIndexAllField(String index, String indexType, String jsonData, Integer id) {
		TransportClient client = null;
		if (Strings.isNullOrEmpty(jsonData)) {
			return;
		}
		try {
			EsClientPool newEsClient = EsClientPool.getInstance();
    		client = newEsClient.getTransPortClient();
    		client.prepareUpdate(index, indexType, id+"")
			.setDoc(jsonData)
			.execute()
			.actionGet();
		} catch (Exception e) {
			logger.error("jsonData={}, 更新数据失败!", jsonData, e);
			throw new RuntimeException(e);
		} finally {
			if (client != null) {
				EsClientPool.getInstance().releaseClient(client);
			}
		}
	}
	
	/**
	 * 向ES添加数据,如果数据存在则更新
	 * @param client  ES连接
	 * @param index 索引库名称（相当于数据库概念）
	 * @param indexType 类型名称 （相当于表的概念）
	 * @param jsonData json格式的数据
	 * @param id 数据的主键ID
	 */
	public void addIndexObj(String index, String indexType
			, String jsonData, String id){
		TransportClient client = null;
		if (Strings.isNullOrEmpty(jsonData)) {
			return;
		}
		try {
			EsClientPool newEsClient = EsClientPool.getInstance();
    		client = newEsClient.getTransPortClient();
    		client.prepareIndex(index, indexType)  
		            .setSource(jsonData)
		            .setId(id) // 必须为对象单独指定ID
		            .execute()
		            .actionGet();
		} catch (Exception e) {
			if (client != null) {
				EsClientPool.getInstance().releaseClient(client);
			}
			try {
				EsClientPool newEsClient = EsClientPool.getInstance();
				client = newEsClient.getTransPortClient();
				client.prepareIndex(index, indexType)  
				        .setSource(jsonData)
				        .setId(id) // 必须为对象单独指定ID
				        .execute()
				        .actionGet();
			} catch (Exception e1) {
				logger.error("jsonData={}, 添加数据失败!", jsonData, e1);
				throw new RuntimeException(e1);
			}
		} finally {
			if (client != null) {
				EsClientPool.getInstance().releaseClient(client);
			}
		}
	}
	
	/**
	 * 删除某条记录
	 * @param client
	 * @param index 索引库名称（相当于数据库概念）
	 * @param indexType 类型名称 （相当于表的概念
	 * @param id 主键ID
	 */
	public void deleteIndexObj(String index, String indexType, String id) {
		TransportClient client = null;
		try {
			EsClientPool newEsClient = EsClientPool.getInstance();
    		client = newEsClient.getTransPortClient();
		    client.prepareDelete(index, indexType, id)  
		               .execute().actionGet();
		} catch (Exception e) {
			if (client != null) {
				EsClientPool.getInstance().releaseClient(client);
			}
			try {
				EsClientPool newEsClient = EsClientPool.getInstance();
				client = newEsClient.getTransPortClient();
				 client.prepareDelete(index, indexType, id)  
	               .execute().actionGet();
			} catch (Exception e1) {
				logger.error("e1={}, 删除数据失败!", e1);
				throw new RuntimeException(e1);
			}
		} finally {
			if (client != null) {
				EsClientPool.getInstance().releaseClient(client);
			}
		}
	}
	
	/**
	 * 获取某条记录
	 * @param client
	 * @param index 索引库名称（相当于数据库概念）
	 * @param indexType 类型名称 （相当于表的概念
	 * @param id 主键ID
	 */
	public String getIndexObj(String index, String indexType, String id) {
		TransportClient client = null;
		try {
			EsClientPool newEsClient = EsClientPool.getInstance();
    		client = newEsClient.getTransPortClient();
	        GetResponse response = client.prepareGet(index, indexType, id)  
	                .execute().actionGet();
	        return response.getSourceAsString();
		} catch (Exception e) {
			logger.error("获取数据失败！id={}", id, e);
			throw new RuntimeException(e);
		} finally {
			if (client != null) {
				EsClientPool.getInstance().releaseClient(client);
			}
		}
	}
	
	/**
	 * 获取多条记录
	 * @param client
	 * @param index 索引库名称（相当于数据库概念）
	 * @param indexType 类型名称 （相当于表的概念
	 * @param ids 主键ID集合
	 */
	public List<String> indexObjList(String index, String indexType, String[] ids) {
		TransportClient client = null;
		try {
			EsClientPool newEsClient = EsClientPool.getInstance();
    		client = newEsClient.getTransPortClient();
    		MultiGetResponse multiGetItemResponses = client.prepareMultiGet()
    				.add(index, indexType, ids)
    			    .get();
    		List<String> listResponse = new ArrayList<String>();
			for (MultiGetItemResponse itemResponse : multiGetItemResponses) {
				
			    GetResponse response = itemResponse.getResponse();
			    if (response.isExists()) {                   
			    	listResponse.add(response.getSourceAsString()); 
			    }
			}
			return listResponse;
		} catch (Exception e) {
			logger.error("获取数据失败！ids={}", ids.toString(), e);
			throw new RuntimeException(e);
		} finally {
			if (client != null) {
				EsClientPool.getInstance().releaseClient(client);
			}
		}
	}
	
	/**
	 * 同演员，同导演
	 * @param index
	 * @param indexType
	 * @param value
	 * @param videoType
	 * @param start
	 * @param limit
	 * @param fieldNames
	 * @return
	 * @throws Exception
	 */
	public QueryResult listSameDataByFields(String index, String indexType
			, String value, Integer videoType, int start, int limit, String... fieldNames) throws Exception {
		TransportClient client = null;
		try {
			EsClientPool newEsClient = EsClientPool.getInstance();
    		client = newEsClient.getTransPortClient();
    		BoolQueryBuilder queryCon = null;
    		if (VideoTypeEnum.isLongVideo(videoType.byteValue())) {
    			queryCon = longVideoQuery(videoType.byteValue(), value, fieldNames);
			} else {
				queryCon = shortVideoQuery(value, null, fieldNames);
			}
			
			QueryResult queryResult = new QueryResult();
			// 长视频总数
			long videoTotal = count(client, index, indexType, queryCon);
			queryResult.setTotal(videoTotal);
			
			if (queryCon != null) {
				SearchResponse searchResponse = client.prepareSearch(index)
		                .setTypes(indexType)
		                .setQuery(queryCon)
		                .setSize(limit)
		                .setFrom(start)
		                .execute()
		                .actionGet();
				
				queryResult.setObj(searchResponse.getHits());
			}
	        return queryResult;
			
		} catch (Exception e) {
			logger.error("同演员，同导演，多字段模糊查询数据失败!", e);
			throw new RuntimeException(e);
		} finally {
			if (client != null) {
				EsClientPool.getInstance().releaseClient(client);
			}
		}
	}
	
	/**
	 * 多字段模糊查询(1对多),长视频
	 * @param client
	 * @param index 索引库名称（相当于数据库概念）
	 * @param indexType 类型名称 （相当于表的概念
	 * @param value 匹配的值
	 * @param start 从哪条开始显示 (分页)
	 * @param limit 一页显示多少条数据(分页)
	 * @param fieldNames 要匹配的字段属性
	 * @return
	 */
	public QueryResult listLongLikeDataByFields(String index, String indexType
			, String value, Integer videoType, int start, int limit, String... fieldNames) throws Exception {
		TransportClient client = null;
		try {
			EsClientPool newEsClient = EsClientPool.getInstance();
    		client = newEsClient.getTransPortClient();
			BoolQueryBuilder shortQuery = shortVideoQuery(value, null, fieldNames);
			BoolQueryBuilder longQuery = longVideoQuery(null, value, fieldNames);
			BoolQueryBuilder tvLongQuery = longVideoQuery(VideoTypeEnum.TV_PLAY_TYPE.getState(), value, fieldNames);
			BoolQueryBuilder movieLongQuery = longVideoQuery(VideoTypeEnum.MOVIE_TYPE.getState(), value, fieldNames);
			BoolQueryBuilder varietyLongQuery = longVideoQuery(VideoTypeEnum.VARIETY_TYPE.getState(), value, fieldNames);
			BoolQueryBuilder cartoonLongQuery = longVideoQuery(VideoTypeEnum.CARTOON_TYPE.getState(), value, fieldNames);
			QueryResult queryResult = new QueryResult();
			// 长视频总数
			long longVideoTotal = count(client, index, indexType, longQuery);
			long tvLongVideoTotal = count(client, index, indexType, tvLongQuery);
			long movieLongVideoTotal = count(client, index, indexType, movieLongQuery);
			long varietyLongVideoTotal = count(client, index, indexType, varietyLongQuery);
			long cartoonLongVideoTotal = count(client, index, indexType, cartoonLongQuery);
			// 短视频总数
			long shortVideoTotal = count(client, index, indexType, shortQuery);
			queryResult.setTotal(shortVideoTotal + longVideoTotal);
			queryResult.setLongVideoTotal(longVideoTotal);
			queryResult.setShortVideoTotal(shortVideoTotal);
			queryResult.setTvLongVideoTotal(tvLongVideoTotal);
			queryResult.setMovieLongVideoTotal(movieLongVideoTotal);
			queryResult.setVarietyLongVideoTotal(varietyLongVideoTotal);
			queryResult.setCartoonLongVideoTotal(cartoonLongVideoTotal);
			
			QueryBuilder searchQuery = null;
			if (videoType == null || videoType == -1) {
				searchQuery = longQuery;
			} else if (videoType.byteValue() == VideoTypeEnum.TV_PLAY_TYPE.getState()) {
				searchQuery = tvLongQuery;
			} else if (videoType.byteValue() == VideoTypeEnum.MOVIE_TYPE.getState()) {
				searchQuery = movieLongQuery;
			} else if (videoType.byteValue() == VideoTypeEnum.VARIETY_TYPE.getState()) {
				searchQuery = varietyLongQuery;
			} else if (videoType.byteValue() == VideoTypeEnum.CARTOON_TYPE.getState()) {
				searchQuery = cartoonLongQuery;
			}
			if (searchQuery != null) {
				SearchResponse searchResponse = client.prepareSearch(index)
		                .setTypes(indexType)
		                .setQuery(searchQuery)
		                .setSize(limit)
		                .setFrom(start)
		                .addSort("_score", SortOrder.DESC)
		                .addSort(SortBuilders.fieldSort("updateTime").order(SortOrder.DESC))
		                .execute()
		                .actionGet();
				
				queryResult.setObj(searchResponse.getHits());
			}
	        return queryResult;
			
		} catch (Exception e) {
			logger.error("多字段模糊查询数据失败!", e);
			throw new RuntimeException(e);
		} finally {
			if (client != null) {
				EsClientPool.getInstance().releaseClient(client);
			}
		}
	}
	
	/**
	 * 组装长视频的搜索条件
	 * @param value
	 * @param fieldNames
	 * @return
	 */
	private BoolQueryBuilder longVideoQuery(Byte longType, String value, String... fieldNames) {
		BoolQueryBuilder query1 = QueryBuilders.boolQuery();
		query1.must(QueryBuilders.termQuery("status", StatusConstants.STATUS_YES)); // 影片必须为上架的状态, 1表示正常
		
		BoolQueryBuilder query2 = QueryBuilders.boolQuery();
		// gt:大于, gte:大于等于, lt:小于, lte:小于等于
		Date date = new Date();
		query2.must(QueryBuilders.rangeQuery("startTime").lte(date.getTime())); // 确保查询出的影片在有效期之内
		query2.must(QueryBuilders.rangeQuery("endTime").gte(date.getTime())); // 确保查询出的影片在有效期之内
		query2.must(QueryBuilders.termQuery("status", StatusConstants.STATUS_YES));
		
		BoolQueryBuilder query3 = QueryBuilders.boolQuery();
		query3.must(QueryBuilders.boolQuery().should(query1).should(query2));
		query3.must(QueryBuilders.multiMatchQuery(value, fieldNames).slop(0));
		query3.must(QueryBuilders.existsQuery("videoBigimg"));
		if (longType != null) {
			query3.must(QueryBuilders.termQuery("videoType", longType));
		} else {
			query3.mustNot(QueryBuilders.termQuery("videoType", VideoTypeEnum.CHILDREN_TYPE.getState())); // 少儿--表示短视频
			query3.mustNot(QueryBuilders.termQuery("videoType", VideoTypeEnum.ENTERTAIN_TYPE.getState())); // 娱乐--表示短视频
			query3.mustNot(QueryBuilders.termQuery("videoType", VideoTypeEnum.GAME_TYPE.getState())); // 游戏--表示短视频
			query3.mustNot(QueryBuilders.termQuery("videoType", VideoTypeEnum.SPORT_TYPE.getState())); // 运动--表示短视频
			query3.mustNot(QueryBuilders.termQuery("videoType", VideoTypeEnum.FUN_TYPE.getState())); // 搞笑--表示短视频
			query3.mustNot(QueryBuilders.termQuery("videoType", VideoTypeEnum.SHORT_TYPE.getState())); // 短视频
			query3.mustNot(QueryBuilders.termQuery("videoType", VideoTypeEnum.PIANHUA_TYPE.getState())); // 片花--短视频
			query3.mustNot(QueryBuilders.termQuery("videoType", VideoTypeEnum.FASHION_TYPE.getState())); // 时尚--短视频
			query3.mustNot(QueryBuilders.termQuery("videoType", VideoTypeEnum.ORIGINAL_TYPE.getState())); // 原创--短视频
		}
		return query3;
	}
	
	/**
	 * 组装短视频的搜索条件
	 * @param value
	 * @param fieldAndValues
	 * @param fieldNames
	 * @return
	 */
	private BoolQueryBuilder shortVideoQuery(String value, List<EsParam> fieldAndValues, String... fieldNames) {
		BoolQueryBuilder query1 = QueryBuilders.boolQuery();
		query1.must(QueryBuilders.termQuery("status", StatusConstants.STATUS_YES)); // 影片必须为上架的状态, 1表示正常
		
		BoolQueryBuilder query2 = QueryBuilders.boolQuery();
		// gt:大于, gte:大于等于, lt:小于, lte:小于等于
		Date date = new Date();
		query2.must(QueryBuilders.rangeQuery("startTime").lte(date.getTime())); // 确保查询出的影片在有效期之内
		query2.must(QueryBuilders.rangeQuery("endTime").gte(date.getTime())); // 确保查询出的影片在有效期之内
		query2.must(QueryBuilders.termQuery("status", StatusConstants.STATUS_YES));
		
		BoolQueryBuilder query3 = QueryBuilders.boolQuery();
		query3.must(QueryBuilders.boolQuery().should(query1).should(query2));
		query3.must(QueryBuilders.multiMatchQuery(value, fieldNames).slop(0));
		// 第三方的影片横图，用的是imageLink字段， 2,3,6都是第三方的
		query3.must(QueryBuilders.existsQuery("videoThumbimg"));
		
		query3.mustNot(QueryBuilders.termQuery("videoType", VideoTypeEnum.TV_PLAY_TYPE.getState()));// 电视剧
		query3.mustNot(QueryBuilders.termQuery("videoType", VideoTypeEnum.CARTOON_TYPE.getState()));// 卡通
		query3.mustNot(QueryBuilders.termQuery("videoType", VideoTypeEnum.MOVIE_TYPE.getState()));// 电影
		query3.mustNot(QueryBuilders.termQuery("videoType", VideoTypeEnum.VARIETY_TYPE.getState()));// 综艺
		query3.mustNot(QueryBuilders.termQuery("videoType", VideoTypeEnum.TALK_TYPE.getState()));// 脱口秀
		if (fieldAndValues != null) {
			for (EsParam param : fieldAndValues) {
				if (param.getIsLike()) {
					query3.must(QueryBuilders.multiMatchQuery(param.getValue(), param.getField()).slop(0));
				} else {
					if ("duration".equals(param.getField())) {
						// 0-10分钟, 10-30分钟, 30分钟-1小时, 1小时以上
						if (Duration.LESS_THAN_TEN.equals(param.getValue())) {
							query3.must(QueryBuilders.rangeQuery("videoDuration").from(0).to(600));
						} else if (Duration.TEN_TO_THRITY.equals(param.getValue())) {
							query3.must(QueryBuilders.rangeQuery("videoDuration").from(600).to(1800));
						} else if (Duration.THIRTY_TO_ONE_HOUR.equals(param.getValue())) {
							query3.must(QueryBuilders.rangeQuery("videoDuration").from(1800).to(3600));
						} else if (Duration.MORE_THAN_ONE_HOUE.equals(param.getValue())) {
							query3.must(QueryBuilders.rangeQuery("videoDuration").gt(3600));
						}
						
					} else {
						query3.must(QueryBuilders.termQuery(param.getField(), param.getValue()));
					}
				}				
			}
		}
		return query3;
	}
	
	
	/**
	 * 多字段模糊查询(1对多) 短视频
	 * @param client
	 * @param index 索引库名称（相当于数据库概念）
	 * @param indexType 类型名称 （相当于表的概念
	 * @param value 匹配的值
	 * @param start 从哪条开始显示 (分页)
	 * @param limit 一页显示多少条数据(分页)
	 * @param fieldNames 要匹配的字段属性
	 * @return
	 */
	public QueryResult listShortLikeDataByFields(String index, String indexType
			, String value, List<EsParam> fieldAndValues, int start, int limit, String... fieldNames) throws Exception {
		TransportClient client = null;
		try {
			EsClientPool newEsClient = EsClientPool.getInstance();
    		client = newEsClient.getTransPortClient();
			BoolQueryBuilder shortQuery = shortVideoQuery(value, fieldAndValues, fieldNames);
			BoolQueryBuilder longQuery = longVideoQuery(null, value, fieldNames);
			BoolQueryBuilder tvLongQuery = longVideoQuery(VideoTypeEnum.TV_PLAY_TYPE.getState(), value, fieldNames);
			BoolQueryBuilder movieLongQuery = longVideoQuery(VideoTypeEnum.MOVIE_TYPE.getState(), value, fieldNames);
			BoolQueryBuilder varietyLongQuery = longVideoQuery(VideoTypeEnum.VARIETY_TYPE.getState(), value, fieldNames);
			BoolQueryBuilder cartoonLongQuery = longVideoQuery(VideoTypeEnum.CARTOON_TYPE.getState(), value, fieldNames);
			QueryResult queryResult = new QueryResult();
			// 长视频总数
			long longVideoTotal = count(client, index, indexType, longQuery);
			long tvLongVideoTotal = count(client, index, indexType, tvLongQuery);
			long movieLongVideoTotal = count(client, index, indexType, movieLongQuery);
			long varietyLongVideoTotal = count(client, index, indexType, varietyLongQuery);
			long cartoonLongVideoTotal = count(client, index, indexType, cartoonLongQuery);
			// 短视频总数
			long shortVideoTotal = count(client, index, indexType, shortQuery);
			queryResult.setTotal(shortVideoTotal + longVideoTotal);
			queryResult.setLongVideoTotal(longVideoTotal);
			queryResult.setShortVideoTotal(shortVideoTotal);
			queryResult.setTvLongVideoTotal(tvLongVideoTotal);
			queryResult.setMovieLongVideoTotal(movieLongVideoTotal);
			queryResult.setVarietyLongVideoTotal(varietyLongVideoTotal);
			queryResult.setCartoonLongVideoTotal(cartoonLongVideoTotal);
			SearchResponse searchResponse = client.prepareSearch(index)
	                .setTypes(indexType)
	                .setQuery(shortQuery)
	                .setSize(limit)
	                .setFrom(start)
	                .addSort("_score", SortOrder.DESC)
	                .addSort(SortBuilders.fieldSort("updateTime").order(SortOrder.DESC))
	                .execute()
	                .actionGet();
			
			queryResult.setObj(searchResponse.getHits());
	        return queryResult;
			
		} catch (Exception e) {
			logger.error("短视频，多字段模糊查询数据失败!", e);
			throw new RuntimeException(e);
		} finally {
			if (client != null) {
				EsClientPool.getInstance().releaseClient(client);
			}
		}
	}
	
	/**
	 * 短视频片库
	 * @param index
	 * @param indexType
	 * @param fieldAndValues
	 * @param start
	 * @param limit
	 * @return
	 * @throws Exception
	 */
	public QueryResult listShortVideoSortByTime(String index, String indexType
			, List<EsParam> fieldAndValues, int start, int limit) throws Exception {
		TransportClient client = null;
		try {
			BoolQueryBuilder query = QueryBuilders.boolQuery();
			BoolQueryBuilder query1 = QueryBuilders.boolQuery();
			query1.must(QueryBuilders.termQuery("status", StatusConstants.STATUS_YES)); // 影片必须为上架的状态, 1表示正常
			
			BoolQueryBuilder query2 = QueryBuilders.boolQuery();
			// gt:大于, gte:大于等于, lt:小于, lte:小于等于
			Date date = new Date();
			query2.must(QueryBuilders.rangeQuery("startTime").lte(date.getTime())); // 确保查询出的影片在有效期之内
			query2.must(QueryBuilders.rangeQuery("endTime").gte(date.getTime())); // 确保查询出的影片在有效期之内
			query2.must(QueryBuilders.termQuery("status", StatusConstants.STATUS_YES));
			
			// 这两个条件满足一个就可以了
			query.must(QueryBuilders.boolQuery().should(query1).should(query2));
			// 第三方的影片横图，用的是imageLink字段， 2,3,6都是第三方的
			query.must(QueryBuilders.existsQuery("videoThumbimg"));
			for (EsParam param : fieldAndValues) {
				if (param.getIsLike()) {
					if ("videoTag".equals(param.getField())) {
						String tag = (String)param.getValue();
						if (tag.equals("其他")) {
							BoolQueryBuilder query11 = QueryBuilders.boolQuery();
							query11.should((QueryBuilders.termQuery(param.getField(), "其他")));
							query11.should(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery(param.getField())));
							query.must(query11);
						} else {
							query.must(QueryBuilders.multiMatchQuery(param.getValue(), param.getField()).slop(0));
						}
					} 
					
				} else {
					if ("duration".equals(param.getField())) {
						// 0-10分钟, 10-30分钟, 30分钟-1小时, 1小时以上
						if (Duration.LESS_THAN_TEN.equals(param.getValue())) {
							query.must(QueryBuilders.rangeQuery("videoDuration").from(0).to(600));
						} else if (Duration.TEN_TO_THRITY.equals(param.getValue())) {
							query.must(QueryBuilders.rangeQuery("videoDuration").from(600).to(1800));
						} else if (Duration.THIRTY_TO_ONE_HOUR.equals(param.getValue())) {
							query.must(QueryBuilders.rangeQuery("videoDuration").from(1800).to(3600));
						} else if (Duration.MORE_THAN_ONE_HOUE.equals(param.getValue())) {
							query.must(QueryBuilders.rangeQuery("videoDuration").gt(3600));
						}
						
					} else if ("videoType".equals(param.getField())) {
						if ((Integer)param.getValue() == VideoTypeEnum.SHORT_TYPE.getState()) {
							query.mustNot(QueryBuilders.termQuery("videoType", VideoTypeEnum.TV_PLAY_TYPE.getState()));// 电视剧
							query.mustNot(QueryBuilders.termQuery("videoType", VideoTypeEnum.CARTOON_TYPE.getState()));// 卡通
							query.mustNot(QueryBuilders.termQuery("videoType", VideoTypeEnum.MOVIE_TYPE.getState()));// 电影
							query.mustNot(QueryBuilders.termQuery("videoType", VideoTypeEnum.VARIETY_TYPE.getState()));// 娱乐
							query.mustNot(QueryBuilders.termQuery("videoType", VideoTypeEnum.TALK_TYPE.getState()));// 脱口秀
						} else {
							query.must(QueryBuilders.termQuery(param.getField(), param.getValue()));
						}
					}
				}				
			}
			
			EsClientPool newEsClient = EsClientPool.getInstance();
    		client = newEsClient.getTransPortClient();
			SearchRequestBuilder searchRequestBuilder =
					client.prepareSearch(index)
					.setTypes(indexType)
					.setQuery(query)
					.setSize(limit)
					.setFrom(start);
			searchRequestBuilder.addSort(SortBuilders.fieldSort("updateTime").order(SortOrder.DESC));
			SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
			long total = count(client, index, indexType, query);
			QueryResult queryResult = new QueryResult();
			queryResult.setObj(searchResponse.getHits());
			queryResult.setTotal(total);
			
	        return queryResult;
			
		} catch (Exception e) {
			logger.error("短视频，片库查询数据失败!", e);
			throw new RuntimeException(e);
		} finally {
			if (client != null) {
				EsClientPool.getInstance().releaseClient(client);
			}
		}
	}
	
	/**
	 * 单字段模糊查询(1对1)
	 * @param client
	 * @param index 索引库名称（相当于数据库概念）
	 * @param indexType 类型名称 （相当于表的概念
	 * @param fieldName 要匹配的字段属性
	 * @param value 匹配的值
	 * @param start 从哪条开始显示(分页)
	 * @param limit 一页显示多少条数据(分页)
	 * @return
	 */
	public QueryResult listLikeDataBySingleField(String index, String indexType, String fieldName, String value, int start, int limit) throws Exception {
		TransportClient client = null;
		try {
			BoolQueryBuilder query = QueryBuilders.boolQuery();
			BoolQueryBuilder query1 = QueryBuilders.boolQuery();
			query1.must(QueryBuilders.termQuery("status", StatusConstants.STATUS_YES)); // 影片必须为上架的状态, 1表示正常
			query1.must(QueryBuilders.matchPhraseQuery(fieldName, value).slop(10));
			
			BoolQueryBuilder query2 = QueryBuilders.boolQuery();
			// gt:大于, gte:大于等于, lt:小于, lte:小于等于
			Date date = new Date();
			query2.must(QueryBuilders.rangeQuery("startTime").lte(date.getTime())); // 确保查询出的影片在有效期之内
			query2.must(QueryBuilders.rangeQuery("endTime").gte(date.getTime())); // 确保查询出的影片在有效期之内
			query2.must(QueryBuilders.termQuery("status", StatusConstants.STATUS_YES));
			query2.must(QueryBuilders.matchPhraseQuery(fieldName, value).slop(10));
			
			// 这两个条件满足一个就可以了
			query.should(query1); // 第三方影片有个上下架的状态
			query.should(query2); // 自有影片没有上下架状态，但是它有过期时间
			EsClientPool newEsClient = EsClientPool.getInstance();
    		client = newEsClient.getTransPortClient();
			SearchResponse searchResponse = client.prepareSearch(index)
	                .setTypes(indexType)
	                .setQuery(query)
	                .setSize(limit)
	                .setFrom(start)
	                .execute()
	                .actionGet();
			
			long total = count(client, index, indexType, query);
			QueryResult queryResult = new QueryResult();
			queryResult.setObj(searchResponse.getHits());
			queryResult.setTotal(total);
			
	        return queryResult;
		} catch (Exception e) {
			logger.error("单字段模糊匹配查询失败!", e);
			throw new RuntimeException(e);
		} finally {
			if (client != null) {
				EsClientPool.getInstance().releaseClient(client);
			}
		}
	}
	
	/**
	 * 多字段精确和模糊匹配混合查询(按更新时间排序)
	 * @param client
	 * @param index 索引库名称（相当于数据库概念）
	 * @param indexType 类型名称 （相当于表的概念
	 * @param fieldAndValues 字段和值得集合
	 * @param start
	 * @param limit
	 * @return
	 */
	public QueryResult listMustAndLikeDataByFieldsSortByTime(String index, String indexType
			, List<EsParam> fieldAndValues, Boolean isHotest, int start, int limit) throws Exception {
		TransportClient client = null;
		if (fieldAndValues != null && fieldAndValues.size() != 0) {
			try {
				BoolQueryBuilder query = QueryBuilders.boolQuery();
				BoolQueryBuilder query1 = QueryBuilders.boolQuery();
				query1.must(QueryBuilders.termQuery("status", StatusConstants.STATUS_YES)); // 影片必须为上架的状态, 1表示正常
				
				BoolQueryBuilder query2 = QueryBuilders.boolQuery();
				// gt:大于, gte:大于等于, lt:小于, lte:小于等于
				Date date = new Date();
				query2.must(QueryBuilders.rangeQuery("startTime").lte(date.getTime())); // 确保查询出的影片在有效期之内
				query2.must(QueryBuilders.rangeQuery("endTime").gte(date.getTime())); // 确保查询出的影片在有效期之内
				query2.must(QueryBuilders.termQuery("status", StatusConstants.STATUS_YES));
				
				// 这两个条件满足一个就可以了
				query.must(QueryBuilders.boolQuery().should(query1).should(query2));
				
				for (EsParam param : fieldAndValues) {
					if (param.getIsLike()) {
						if ("videoTag".equals(param.getField())) {
							String tag = (String)param.getValue();
							if (tag.equals("其他")) {
								BoolQueryBuilder query11 = QueryBuilders.boolQuery();
								query11.should((QueryBuilders.termQuery(param.getField(), "其他")));
								query11.should(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery(param.getField())));
								query.must(query11);
							} else {
								query.must(QueryBuilders.multiMatchQuery(param.getValue(), param.getField()).slop(0));
								/*if (param.getValue().toString().contains("剧")) {
									query.must(QueryBuilders.multiMatchQuery(param.getValue(), param.getField()).slop(10));
								} else {
									BoolQueryBuilder query11 = QueryBuilders.boolQuery();
									query11.should(QueryBuilders.multiMatchQuery(param.getValue(), param.getField()).slop(10));
									query11.should(QueryBuilders.multiMatchQuery(param.getValue()+"剧", param.getField()).slop(10));
									query.must(query11);
								}*/
							}
						} 
						
					} else {
						if ("duration".equals(param.getField())) {
							// 0-10分钟, 10-30分钟, 30分钟-1小时, 1小时以上
							if (Duration.LESS_THAN_TEN.equals(param.getValue())) {
								query.must(QueryBuilders.rangeQuery("videoDuration").from(0).to(600));
							} else if (Duration.TEN_TO_THRITY.equals(param.getValue())) {
								query.must(QueryBuilders.rangeQuery("videoDuration").from(600).to(1800));
							} else if (Duration.THIRTY_TO_ONE_HOUR.equals(param.getValue())) {
								query.must(QueryBuilders.rangeQuery("videoDuration").from(1800).to(3600));
							} else if (Duration.MORE_THAN_ONE_HOUE.equals(param.getValue())) {
								query.must(QueryBuilders.rangeQuery("videoDuration").gt(3600));
							}
							
						} else if ("videoYear".equals(param.getField())) {
							String yearValue = (String)param.getValue();
							if ("其他".equals(yearValue)) {
								BoolQueryBuilder query11 = QueryBuilders.boolQuery();
								query11.should((QueryBuilders.rangeQuery(param.getField()).lt(1980)));
								query11.should(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery(param.getField())));
								query.must(query11); // 小于80年代
							} else if ("80年代".equals(yearValue)) {
								query.must(QueryBuilders.rangeQuery(param.getField()).from(1980).to(1989)); // 80年代
							} else if ("90年代".equals(yearValue)) {
								query.must(QueryBuilders.rangeQuery(param.getField()).from(1990).to(1999)); // 80年代
							} else if (yearValue.length() == 4) {
								query.must(QueryBuilders.termQuery(param.getField(), param.getValue()));
							} else {
								int endTime = Integer.parseInt(yearValue.split("-")[0]);
								int startTime = Integer.parseInt(yearValue.split("-")[1]);
								query.must(QueryBuilders.rangeQuery(param.getField()).from(startTime).to(endTime)); // 80年代
							}
						} else if ("videoArea.noanVideoArea".equals(param.getField())) {
							String areaValue = (String)param.getValue();
							if ("大陆".equals(areaValue) || "内陆".equals(areaValue) || "内地".equals(areaValue)) {
								BoolQueryBuilder query11 = QueryBuilders.boolQuery();
								query11.should((QueryBuilders.termQuery(param.getField(), "内地")));
								query11.should((QueryBuilders.termQuery(param.getField(), "内地剧")));
								query.must(query11);
								// query.must(QueryBuilders.termQuery(param.getField(), "内地"));
								
							} else if ("其它".equals(areaValue) || "其他".equals(areaValue)) {
								BoolQueryBuilder query11 = QueryBuilders.boolQuery();
								query11.should((QueryBuilders.termQuery(param.getField(), "其他")));
								query11.should(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery(param.getField())));
								query.must(query11);
							} else if ("台湾".equals(areaValue)) {
								BoolQueryBuilder query11 = QueryBuilders.boolQuery();
								query11.should((QueryBuilders.termQuery(param.getField(), "台湾")));
								query11.should((QueryBuilders.termQuery(param.getField(), "港台")));
								query.must(query11);
							} else if ("香港".equals(areaValue)) {
								BoolQueryBuilder query11 = QueryBuilders.boolQuery();
								query11.should((QueryBuilders.termQuery(param.getField(), "香港")));
								query11.should((QueryBuilders.termQuery(param.getField(), "港台")));
								query.must(query11);
							} else if ("日本".equals(areaValue)) {
								BoolQueryBuilder query11 = QueryBuilders.boolQuery();
								query11.should((QueryBuilders.termQuery(param.getField(), "日韩")));
								query11.should((QueryBuilders.termQuery(param.getField(), "日本")));
								query.must(query11);
							} else if ("韩国".equals(areaValue)) {
								BoolQueryBuilder query11 = QueryBuilders.boolQuery();
								query11.should((QueryBuilders.termQuery(param.getField(), "韩国")));
								query11.should((QueryBuilders.termQuery(param.getField(), "日韩")));
								query.must(query11);
							} else {
								query.must(QueryBuilders.termQuery(param.getField(), param.getValue()));
							}
							
						} else {
							query.must(QueryBuilders.termQuery(param.getField(), param.getValue()));
						}
					}				
				}
				
				EsClientPool newEsClient = EsClientPool.getInstance();
	    		client = newEsClient.getTransPortClient();
				SearchRequestBuilder searchRequestBuilder =
						client.prepareSearch(index)
						.setTypes(indexType)
						.setQuery(query)
						.setSize(limit)
						.setFrom(start);
				if (isHotest) {
					searchRequestBuilder.addSort(SortBuilders.fieldSort("hot").order(SortOrder.DESC))
					.addSort(SortBuilders.fieldSort("updateTime").order(SortOrder.DESC));
				} else {
					searchRequestBuilder.addSort(SortBuilders.fieldSort("updateTime").order(SortOrder.DESC));
				}
				SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
				long total = count(client, index, indexType, query);
				QueryResult queryResult = new QueryResult();
				queryResult.setObj(searchResponse.getHits());
				queryResult.setTotal(total);
				
		        return queryResult;
		       
			} catch (Exception e) {
				logger.error("按时间排序，多字段精确和模糊匹配混合查询失败!", e);
				throw new RuntimeException(e);
			} finally {
				if (client != null) {
					EsClientPool.getInstance().releaseClient(client);
				}
			}
		}
		return null;
	}
	
	/**
	 * 统计查询总数（分页的时候需要用到）
	 * @param client
	 * @param index 索引库名称（相当于数据库概念）
	 * @param indexType 类型名称 （相当于表的概念
	 * @param query
	 * @return
	 */
	private long count(TransportClient client, String index, String indexType, QueryBuilder query) {
		SearchResponse response = client.prepareSearch(index)
				.setTypes(indexType)
					.setQuery(query)//设置查询类型
					.setSearchType(SearchType.QUERY_THEN_FETCH)//设置查询类型，有的版本可能过期
					.setSize(0)//设置返回结果集为0
					.get();
		return response.getHits().totalHits();
	}
}
