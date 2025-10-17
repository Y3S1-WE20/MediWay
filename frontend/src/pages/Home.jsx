import Hero from "../components/Hero";
import PatientStories from "../components/PatientStories";
import Features from "../components/FeaturesSection";
import About from "../components/About";
import Stats from "../components/Stats";
import CTA from "../components/CTA";

const Home = () => {
  return (
    <div className="w-full">
      <Hero />
      <PatientStories />
      <Features />
      <About />
      <Stats />
      <CTA />
    </div>
  );
};

export default Home;
