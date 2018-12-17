package com.mx.santander.commons.channel.dao.db;

import java.util.List;

import com.mx.santander.commons.channel.model.entity.ChannelsEntity;

/**
 * Dao para administracion de collection channels
 * 
 * @author Miguel Angel Garcia Labastida
 *
 */
public interface IChannelsDAO {

    /**
     * busca todos los canales
     * 
     * @return {@link ChannelsEntity}
     */
    List<ChannelsEntity> findAll();

}