package rebue.robotech.svc.impl;

import java.io.IOException;
import java.util.Map;

import javax.annotation.Resource;

import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

import lombok.extern.slf4j.Slf4j;
import rebue.robotech.so.So;
import rebue.robotech.svc.EsBaseSvc;
import rebue.wheel.api.exception.RuntimeExceptionX;
import rebue.wheel.core.GenericTypeUtils;
import rebue.wheel.core.PojoUtils;

@Slf4j
public abstract class EsBaseSvcImpl<SO extends So> implements EsBaseSvc<SO> {

    @Resource
    private RestHighLevelClient esClient;

    @Override
    public SO add(final SO so) {
        log.info("add: so-{}", so);
        try {
            @SuppressWarnings("unchecked")
            final IndexRequest  req  = new IndexRequest(getIndexName()).id(so.getId()).source((Map<String, ?>) PojoUtils.pojoToMap(so));
            final IndexResponse resp = esClient.index(req, RequestOptions.DEFAULT);
            log.info("ElasticSearch添加document成功: index-{} id-{}", resp.getIndex(), resp.getId());
            return so;
        } catch (final IOException e) {
            final String msg = "ElasticSearch添加document失败";
            log.error(msg, e);
            throw new RuntimeExceptionX(msg, e);
        }
    }

    @Override
    public void modify(final SO so) {
        log.info("modify: so-{}", so);
        try {
            @SuppressWarnings("unchecked")
            final UpdateRequest  req  = new UpdateRequest(getIndexName(), so.getId()).doc((Map<String, Object>) PojoUtils.pojoToMap(so));
            final UpdateResponse resp = esClient.update(req, RequestOptions.DEFAULT);
            log.info("ElasticSearch修改document成功: index-{} id-{}", resp.getIndex(), resp.getId());
        } catch (final IOException e) {
            final String msg = "ElasticSearch修改document失败";
            log.error(msg, e);
            throw new RuntimeExceptionX(msg, e);
        }
    }

    @Override
    public void del(final String id) {
        log.info("del: id-{}", id);
        try {
            final DeleteRequest  req  = new DeleteRequest(getIndexName(), id);
            final DeleteResponse resp = esClient.delete(req, RequestOptions.DEFAULT);
            log.info("ElasticSearch删除document成功: index-{} id-{}", resp.getIndex(), resp.getId());
        } catch (final IOException e) {
            final String msg = "ElasticSearch删除document失败";
            log.error(msg, e);
            throw new RuntimeExceptionX(msg, e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public SO getById(final String id) {
        log.info("getById: id-{}", id);
        try {
            final GetRequest  req  = new GetRequest(getIndexName(), id);
            final GetResponse resp = esClient.get(req, RequestOptions.DEFAULT);
            log.info("ElasticSearch通过id获取document成功: index-{} id-{}", resp.getIndex(), resp.getId());
            return (SO) PojoUtils.mapToPojo(resp.getSource(), GenericTypeUtils.getGenericClass(this));
        } catch (final IOException e) {
            final String msg = "ElasticSearch删除document失败";
            log.error(msg, e);
            throw new RuntimeExceptionX(msg, e);
        }
    }

}
