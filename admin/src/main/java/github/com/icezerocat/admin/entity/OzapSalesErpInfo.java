package github.com.icezerocat.admin.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * Description:  
 * Date: 2020-04-04 14:14:19 
 * @author  0.0
 */
@Data
@Entity
@Table ( name ="ozap_sales_erp_info")
public class OzapSalesErpInfo  implements Serializable {

	private static final long serialVersionUID =  5202133546087367919L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
   	@Column(name = "ID" )
	private Long id;

	/**
	 * 地址
	 */
   	@Column(name = "address" )
	private String address;

	/**
	 * 买家昵称
	 */
   	@Column(name = "buyer" )
	private String buyer;

	/**
	 * 账号
	 */
   	@Column(name = "buyer_account" )
	private String buyerAccount;

	/**
	 * 买家电话
	 */
   	@Column(name = "buyer_mobile" )
	private String buyerMobile;

	/**
	 * 买家留言
	 */
   	@Column(name = "buyer_msg" )
	private String buyerMsg;

	/**
	 * 市
	 */
   	@Column(name = "city" )
	private String city;

	/**
	 * 国家
	 */
   	@Column(name = "country" )
	private String country;

	/**
	 * 创建时间
	 */
   	@Column(name = "create_time" )
	private Date createTime;

	/**
	 * 原始货币种类
	 */
   	@Column(name = "currency_code" )
	private String currencyCode;

	/**
	 * 原始货币金额
	 */
   	@Column(name = "currency_sum" )
	private BigDecimal currencySum;

	/**
	 * 优惠金额
	 */
   	@Column(name = "discount_fee" )
	private String discountFee;

	/**
	 * 区
	 */
   	@Column(name = "district" )
	private String district;

	/**
	 * 快递单号
	 */
   	@Column(name = "express_code" )
	private String expressCode;

	/**
	 * 是否已付款
	 */
   	@Column(name = "is_pay" )
	private Long isPay;

	/**
	 * 修改时间
	 */
   	@Column(name = "modify_time" )
	private Date modifyTime;

	/**
	 * 线上状态
	 */
   	@Column(name = "oln_status" )
	private Long oLnStatus;

	/**
	 * 实际支付金额
	 */
   	@Column(name = "paid_fee" )
	private BigDecimal paidFee;

	/**
	 * 付款时间
	 */
   	@Column(name = "pay_time" )
	private String payTime;

	/**
	 * 手机号
	 */
   	@Column(name = "phone" )
	private String phone;

	/**
	 * 万里牛单据处理状态
	 */
   	@Column(name = "process_status" )
	private Long processStatus;

	/**
	 * 省
	 */
   	@Column(name = "province" )
	private String province;

	/**
	 * 收件人
	 */
   	@Column(name = "receiver" )
	private String receiver;

	/**
	 * 备注
	 */
   	@Column(name = "remark" )
	private String remark;

	/**
	 * 业务员
	 */
   	@Column(name = "sale_man" )
	private String saleMan;

	/**
	 * 卖家留言
	 */
   	@Column(name = "seller_msg" )
	private String sellerMsg;

	/**
	 * 店铺ID
	 */
   	@Column(name = "shop_id" )
	private String shopId;

	/**
	 * 店铺名称(页面上显示)
	 */
   	@Column(name = "shop_name" )
	private String shopName;

	/**
	 * 店铺昵称(店铺唯一)
	 */
   	@Column(name = "shop_nick" )
	private String shopNick;

	/**
	 * 订单来源平台
	 */
   	@Column(name = "source_platform" )
	private String sourcePlatform;

	/**
	 * 状态
	 */
   	@Column(name = "status" )
	private Long status;

	/**
	 * 总金额
	 */
   	@Column(name = "sum_sale" )
	private BigDecimal sumSale;

	/**
	 * 电话
	 */
   	@Column(name = "tel" )
	private String tel;

	/**
	 * 线上单号
	 */
   	@Column(name = "tp_tid" )
	private String tpTid;

	/**
	 * 订单编码
	 */
   	@Column(name = "trade_no" )
	private String tradeNo;

	/**
	 * 订单类型
	 */
   	@Column(name = "trade_type" )
	private Long tradeType;

	/**
	 * 邮编
	 */
   	@Column(name = "zip" )
	private String zip;

	/**
	 * 插入批次号
	 */
   	@Column(name = "insert_batch_no" )
	private Long insertBatchNo;

	/**
	 * 更新时间
	 */
   	@Column(name = "update_date" )
	private Date updateDate;

	/**
	 * 合并数量
	 */
   	@Column(name = "merge_number" )
	private Long mergeNumber;

	/**
	 * 审核状态
	 */
   	@Column(name = "erp_status" )
	private String erpStatus;

	/**
	 * 产品类型
	 */
   	@Column(name = "type" )
	private String type;

	/**
	 * 款式编码
	 */
   	@Column(name = "style" )
	private String style;

	/**
	 * 数据统计(5条数据/30件衣服)
	 */
   	@Column(name = "sales_item_count" )
	private String salesItemCount;

}
