import { motion } from "framer-motion";
import { Heart, Shield, Users } from "lucide-react";

const AboutSection = () => {
  return (
    <section className="py-20 px-4">
      <div className="container mx-auto max-w-6xl">
        <div className="grid lg:grid-cols-2 gap-12 items-center">
          {/* Text Content */}
          <motion.div
            initial={{ opacity: 0, x: -20 }}
            whileInView={{ opacity: 1, x: 0 }}
            viewport={{ once: true }}
            transition={{ duration: 0.6 }}
          >
            <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-6">
              About MediWay
            </h2>
            <p className="text-lg text-muted-foreground mb-6 leading-relaxed">
              MediWay is revolutionizing healthcare management in Sri Lanka by bridging 
              the gap between patients and healthcare providers through innovative digital solutions.
            </p>
            <p className="text-lg text-muted-foreground mb-8 leading-relaxed">
              Our platform empowers patients with easy appointment booking, secure digital 
              health records, and seamless payment systems—while helping hospitals deliver 
              more efficient, personalized care. With MediWay, quality healthcare is just a tap away.
            </p>

            <div className="space-y-4">
              <div className="flex items-start gap-4">
                <div className="w-10 h-10 rounded-lg bg-primary/10 flex items-center justify-center flex-shrink-0">
                  <Heart className="w-5 h-5 text-primary" />
                </div>
                <div>
                  <h3 className="font-semibold text-foreground mb-1">Patient-Centered Care</h3>
                  <p className="text-muted-foreground text-sm">
                    Your health and convenience are at the heart of everything we do
                  </p>
                </div>
              </div>

              <div className="flex items-start gap-4">
                <div className="w-10 h-10 rounded-lg bg-primary/10 flex items-center justify-center flex-shrink-0">
                  <Shield className="w-5 h-5 text-primary" />
                </div>
                <div>
                  <h3 className="font-semibold text-foreground mb-1">Secure & Private</h3>
                  <p className="text-muted-foreground text-sm">
                    Your medical data is encrypted and protected with industry-leading security
                  </p>
                </div>
              </div>

              <div className="flex items-start gap-4">
                <div className="w-10 h-10 rounded-lg bg-primary/10 flex items-center justify-center flex-shrink-0">
                  <Users className="w-5 h-5 text-primary" />
                </div>
                <div>
                  <h3 className="font-semibold text-foreground mb-1">Trusted Network</h3>
                  <p className="text-muted-foreground text-sm">
                    Connected with leading hospitals and healthcare providers across Sri Lanka
                  </p>
                </div>
              </div>
            </div>
          </motion.div>

          {/* Stats Cards */}
          <motion.div
            initial={{ opacity: 0, x: 20 }}
            whileInView={{ opacity: 1, x: 0 }}
            viewport={{ once: true }}
            transition={{ duration: 0.6, delay: 0.2 }}
            className="grid grid-cols-2 gap-4"
          >
            <div className="bg-card rounded-2xl p-6 shadow-[0_2px_12px_rgba(0,0,0,0.08)]">
              <div className="text-4xl font-bold text-primary mb-2">10K+</div>
              <div className="text-muted-foreground">Active Patients</div>
            </div>
            <div className="bg-card rounded-2xl p-6 shadow-[0_2px_12px_rgba(0,0,0,0.08)]">
              <div className="text-4xl font-bold text-primary mb-2">50+</div>
              <div className="text-muted-foreground">Partner Hospitals</div>
            </div>
            <div className="bg-card rounded-2xl p-6 shadow-[0_2px_12px_rgba(0,0,0,0.08)]">
              <div className="text-4xl font-bold text-primary mb-2">500+</div>
              <div className="text-muted-foreground">Healthcare Providers</div>
            </div>
            <div className="bg-card rounded-2xl p-6 shadow-[0_2px_12px_rgba(0,0,0,0.08)]">
              <div className="text-4xl font-bold text-primary mb-2">24/7</div>
              <div className="text-muted-foreground">Support Available</div>
            </div>
          </motion.div>
        </div>
      </div>
    </section>
  );
};

export default AboutSection;
