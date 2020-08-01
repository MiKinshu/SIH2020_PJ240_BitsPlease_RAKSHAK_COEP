var mongoose = require("mongoose");
var ReportSchema = new mongoose.Schema({
  officerID:{
      type: String,
      default: ""
  },
  networkId:{
    type: String,
    default: ""
  },
  uid:{
      type:String,
      default: ""
  },
  name:{
    type:String,
    default: ""
  },
  phone:{
      type: String,
      default: ""
  },
  date:{
    type: Date,
    default: Date.now
  },
  msg:{
      type : String,
      default: ""
  },
  type:{
    type: String,
    default: "general"
  },
  loc:{
      type: String,
      default: ""
  }
});
mongoose.model("Report", ReportSchema);

module.exports = mongoose.model("Report");