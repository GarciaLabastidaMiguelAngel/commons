package com.mx.santander.commons.channel.dao.db;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.mx.santander.commons.channel.model.entity.ChannelsEntity;

/**
 * Dao para el manejo de la colletion de channels en mongo DB esta colecion
 * contiene los canales y descripcion de los mismos para validar su acceso a la
 * API de Go Pay
 * 
 * @author Miguel Angel Garcia Labastida
 *
 */
@Repository
public class ChannelsDAO implements IChannelsDAO {
    /**
     * logger de la clase
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelsDAO.class);
    /**
     * conexion con BD mongo
     */
    @Autowired
    private MongoTemplate mongotemplate;

    /**
     * metodo para consultar de la colecion de channels todos los canales y sus
     * descripciones
     */
    @Override
    public List<ChannelsEntity> findAll() {
        LOGGER.info("Se accede a BD por los canales");
        return mongotemplate.findAll(ChannelsEntity.class);
    }

}
